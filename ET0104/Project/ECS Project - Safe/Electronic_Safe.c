#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <math.h>
#include "library.h"
#include "wiringPi.h"
#include <signal.h>
#include <pthread.h>

#define LEDPort 0x3A
#define KbdPort 0x3C
#define LCDPort 0x3B
#define SMPort 0x39

#define Col7Lo 0xF7 // column 7 scan
#define Col6Lo 0xFB // column 6 scan
#define Col5Lo 0xFD // column 5 scan
#define Col4Lo 0xFE // column 4 scan

#define AUDIOFILE "/tmp/blaringalarm.raw"

_Bool clockwise = true;
int i, reading, h;
char inp;
int full_seq_drive[4] = {0x08, 0x04, 0x02, 0x01};

int anti_clockwise[4][4] = {
	{0, 0, 0, 1},
	{0, 0, 1, 0},
	{0, 1, 0, 0},
	{1, 0, 0, 0}};

const unsigned char Bin2LED[] =
	{
		/* 0     1     2    3 */
		0x40, 0x79, 0x24, 0x30,
		/*  4     5     6    7*/
		0x19, 0x12, 0x02, 0x78,
		/*  8     9     A    B*/
		0x00, 0x18, 0x08, 0x03,
		/*  C     D     E    F*/
		0x46, 0x21, 0x06, 0x0E};

const unsigned char ScanTable[12] =
	{
		// 0     1     2     3
		0xB7, 0x7E, 0xBE, 0xDE,
		// 4     5     6     7
		0x7D, 0xBD, 0xDD, 0x7B,
		// 8     9     *     #
		0xBB, 0xDB, 0x77, 0xD7};

unsigned char dac_start[] = {"Running DAC"};
unsigned char dac_stop[] = {"Stopping DAC"};
unsigned char motor_start[] = {"Starting motor"};
unsigned char motor_stop[] = {"Stopping motor"};
unsigned char stop[] = {"Exiting Lab"};
unsigned char image[] = {"Image Slideshow"};

// Declaration of thread condition variable
pthread_t id[3];
// unsigned int start_motor = 0;
// unsigned int stop_motor = 0;
// unsigned int start_dac = 0;
// unsigned int stop_adc = 0;

pthread_t motor_id;
pthread_t dac_id;
// declaring mutex
pthread_mutex_t bus_lock = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t motorlock = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t daclock = PTHREAD_MUTEX_INITIALIZER;

pthread_cond_t t2 = PTHREAD_COND_INITIALIZER;
pthread_cond_t t3 = PTHREAD_COND_INITIALIZER;

void initlcd();
void lcd_writecmd(char cmd);
void LCDprint(char *sptr);
void lcddata(unsigned char cmd);

unsigned char ProcKey();
unsigned char ScanKey();
unsigned char ScanCode;

void *thread_dac(void *value)
{
	dac_id = pthread_self();

	unsigned char data[2];
	unsigned short fn[100];
	float gain = 255.0f;
	float phase = 0.0f;
	float bias = 255.0f; // 1024.0f;
	float freq = 2.0 * (3.14159f) / 4.0;
	unsigned char buffer[1];
	int fileend;
	FILE *ptr;
	ptr = fopen(AUDIOFILE, "rb");

	CM3DeviceInit();
	CM3PortInit(5); // initialise DAC
	printf("Connect Pin 1 and 2 of selection jumper Connector J3\n");

	if ((ptr = fopen(AUDIOFILE, "r")) == NULL)
	{
		perror(AUDIOFILE);
		printf("File cannot be found \n");
		return (EXIT_FAILURE);
	}

	while ((fileend = fgetc(ptr)) != EOF)
	{
		fread(buffer, sizeof(buffer), 2, ptr);
		for (int i = 0; i < 1; i++)
		{
			CM3PortWrite(3, buffer[i]);
			// usleep(1);
		}

		//}
		//}
		pthread_mutex_unlock(&daclock);
		// usleep(100);
	}
}

void *thread_motor(void *value)
{

	int j;

	motor_id = pthread_self();

	while (1)
	{
		pthread_mutex_lock(&motorlock);
		{
			if (clockwise == true)
			{
				for (i = 0; i < 4; i++)
				{
					// pthread_mutex_lock(&bus_lock);
					CM3_outport(SMPort, full_seq_drive[i]);
					// pthread_mutex_unlock(&bus_lock);
					usleep(8000);
				}
			}
			else if (clockwise == false)
			{
				for (i = 3; i >= 0; i--)
				{
					// pthread_mutex_lock(&bus_lock);
					CM3_outport(SMPort, full_seq_drive[i]);
					// pthread_mutex_unlock(&bus_lock);
					usleep(8000);
				}
			}
		}
		pthread_mutex_unlock(&motorlock);
		usleep(100);
	}
}

void *thread_keypad(void *value)
{
	unsigned char i;
	int mutex_check;
	int mutex_check_dac;

	pthread_mutex_lock(&motorlock);
	pthread_mutex_lock(&daclock);

	initlcd();

	char correctPass[] = {'1', '2', '3', '4'}; // correct password
	unsigned char key;						   // scankey
	char passwordEntered[4];				   // password entered
	int keyPressed = 0;						   // number of keys pressed
	int wrong = 0;							   // number of wrong attempts
	int passArrInt = 0;						   // array index
	int breakLock = 0;						   // break out of loop
	int breakUnlock = 0;					   // break out of loop
	char newPass[4];
	int passArrInt2 = 0;

	LCDprint("Enter Password: ");

	while (wrong <= 3)
	{
		if (wrong == 3)
		{
			usleep(100000);
			initlcd();
			LCDprint("SYSTEM GO BOOM!");
			usleep(1000000);
			// usleep(10000000); // 10 seconds, I think
			initlcd();
			LCDprint("         10         ");
			usleep(1000000);
			initlcd();
			LCDprint("         9          ");
			usleep(1000000);
			initlcd();
			LCDprint("         8          ");
			usleep(1000000);
			initlcd();
			LCDprint("         7          ");
			usleep(1000000);
			initlcd();
			LCDprint("         6          ");
			usleep(1000000);
			initlcd();
			LCDprint("         5          ");
			usleep(1000000);
			initlcd();
			LCDprint("         4          ");
			usleep(1000000);
			initlcd();
			LCDprint("         3          ");
			usleep(1000000);
			initlcd();
			LCDprint("         2          ");
			usleep(1000000);
			initlcd();
			LCDprint("         1          ");
			usleep(1000000);

			wrong = 0;
			initlcd();
			LCDprint("Enter Password: ");
		}
		else
		{
			while (passArrInt < 4)
			{
				key = ScanKey();
				usleep(100000);
				if (key == '0' || key == '1' || key == '2' || key == '3' || key == '4' || key == '5' || key == '6' || key == '7' || key == '8' || key == '9')
				{
					LCDprint("*");
					passwordEntered[passArrInt] = key;
					passArrInt++;
					usleep(10000);
				}
			}
			if (passArrInt == 4)
			{
				if (passwordEntered[0] == correctPass[0] && passwordEntered[1] == correctPass[1] && passwordEntered[2] == correctPass[2] && passwordEntered[3] == correctPass[3])
				{
					initlcd();
					LCDprint("Correct Password!");
					usleep(1500000);

					initlcd();
					LCDprint("Welcome Back!");
					usleep(1500000);

					system("DISPLAY=:0.0 pqiv -f /tmp/Vault_Select.jpg &");
					usleep(500000);

					initlcd();
					LCDprint("Press 0 to Unlock");
					lcd_writecmd(0xC0);
					LCDprint("Press 1 to Chg Pass");

					while (breakLock != 1)
					{
						i = ScanKey();
						if (i == '0')
						{
							clockwise = true;
							initlcd();
							LCDprint("Unlocking");
							LCDprint(".");
							usleep(500000);
							LCDprint(".");
							usleep(500000);
							LCDprint(".");
							pthread_mutex_unlock(&motorlock);

							system("DISPLAY=:0.0 pqiv -f /tmp/Vault_1.jpg &");
							usleep(500000);

							system("DISPLAY=:0.0 pqiv -f /tmp/Vault_2.jpg &");
							usleep(500000);

							system("DISPLAY=:0.0 pqiv -f /tmp/Vault_3.jpg &");
							usleep(500000);

							system("DISPLAY=:0.0 pqiv -f /tmp/Vault_4.jpg &");
							usleep(500000);

							system("DISPLAY=:0.0 pqiv -f /tmp/Vault_5.jpg &");
							usleep(500000);

							system("DISPLAY=:0.0 pqiv -f /tmp/Vault_Select_Lock.jpg &");
							usleep(5000000);
							system("DISPLAY=:0.0 pqiv -f /tmp/Vault_Select_Lock.jpg &");

							usleep(1000000);
							pthread_mutex_lock(&motorlock);
							wrong = 0;

							initlcd();
							LCDprint("Press 0 to Lock");
							while (breakUnlock != 1)
							{
								i = ScanKey();
								if (i == '0')
								{
									clockwise = false;
									initlcd();
									LCDprint("Locking");
									LCDprint(".");
									usleep(500000);
									LCDprint(".");
									usleep(500000);
									LCDprint(".");
									pthread_mutex_unlock(&motorlock);

									system("DISPLAY=:0.0 pqiv -f /tmp/Vault_5.jpg &");
									usleep(500000);

									system("DISPLAY=:0.0 pqiv -f /tmp/Vault_4.jpg &");
									usleep(500000);

									system("DISPLAY=:0.0 pqiv -f /tmp/Vault_3.jpg &");
									usleep(500000);

									system("DISPLAY=:0.0 pqiv -f /tmp/Vault_2.jpg &");
									usleep(500000);

									system("DISPLAY=:0.0 pqiv -f /tmp/Vault_1.jpg &");
									usleep(5000000);
									system("DISPLAY=:0.0 pqiv -f /tmp/Vault_1.jpg &");

									usleep(1000000);
									pthread_mutex_lock(&motorlock);
									breakLock = 1;
									breakUnlock = 1;
								}
							}
						}
						else if (i == '1')
						{
							initlcd();
							LCDprint("New Password: ");
							usleep(1000000);
							while (passArrInt2 < 4)
							{
								key = ScanKey();
								usleep(100000);
								if (key == '0' || key == '1' || key == '2' || key == '3' || key == '4' || key == '5' || key == '6' || key == '7' || key == '8' || key == '9')
								{
									LCDprint("*");
									newPass[passArrInt2] = key;
									passArrInt2++;
									usleep(10000);
								}
							}
							if (passArrInt2 == 4)
							{
								correctPass[0] = newPass[0];
								correctPass[1] = newPass[1];
								correctPass[2] = newPass[2];
								correctPass[3] = newPass[3];
								initlcd();
								LCDprint("Password Changed!");
								usleep(1000000);
								breakLock = 1;
								breakUnlock = 1;
								newPass[0] = '\0';
								passArrInt2 = 0;
								wrong = 0;
							}
						}
					}
					breakLock = 0;
					breakUnlock = 0;
					passwordEntered[0] = '\0';
					keyPressed = 0;
					passArrInt = 0;
					wrong = 0;
					initlcd();
					LCDprint("Enter Password: ");
				}
				else
				{
					initlcd();
					LCDprint("Wrong Password!");
					usleep(500000);
					passwordEntered[0] = '\0';
					wrong++;
					keyPressed = 0;
					passArrInt = 0;
					initlcd();
					LCDprint("Enter Password: ");
				}
			}
		}
	}
}

int main(int agrv, char *argc[])
{
	int *ptr;
	system("killall pqiv");
	system("DISPLAY=:0.0 pqiv -f /tmp/Vault_1.jpg &");
	sleep(2);
	CM3DeviceInit();
	CM3DeviceSpiInit(0);

	pthread_create(&id[0], NULL, thread_keypad, NULL);
	pthread_create(&id[1], NULL, thread_motor, NULL);
	pthread_create(&id[2], NULL, thread_dac, NULL);

	pthread_join(id[0], (void **)&ptr);
	pthread_join(id[1], (void **)&ptr);
	pthread_join(id[2], (void **)&ptr);
	return 0;
}

//----------- LCD Functions ----------------

void initlcd(void)
{
	usleep(20000);
	lcd_writecmd(0x30);
	usleep(20000);
	lcd_writecmd(0x30);
	usleep(20000);
	lcd_writecmd(0x30);

	lcd_writecmd(0x02); // 4 bit mode
	lcd_writecmd(0x28); // 2 line  5*7 dots
	lcd_writecmd(0x01); // clear screen
	lcd_writecmd(0x0c); // dis on cur off
	lcd_writecmd(0x06); // inc cur
	lcd_writecmd(0x80);
}

void lcd_writecmd(char cmd)
{
	char data;

	data = (cmd & 0xf0);
	CM3_outport(LCDPort, data | 0x04);
	usleep(10);
	CM3_outport(LCDPort, data);

	usleep(200);

	data = (cmd & 0x0f) << 4;
	CM3_outport(LCDPort, data | 0x04);
	usleep(10);
	CM3_outport(LCDPort, data);

	usleep(2000);
}

void LCDprint(char *sptr)
{
	while (*sptr != 0)
	{
		int i = 1;
		lcddata(*sptr);
		++sptr;
	}
}

void lcddata(unsigned char cmd)
{

	char data;

	data = (cmd & 0xf0);
	CM3_outport(LCDPort, data | 0x05);
	usleep(10);
	CM3_outport(LCDPort, data);

	usleep(200);

	data = (cmd & 0x0f) << 4;
	CM3_outport(LCDPort, data | 0x05);
	usleep(10);
	CM3_outport(LCDPort, data);

	usleep(2000);
}

//----------- Keypad Functions ----------------

unsigned char ScanKey()
{
	CM3_outport(KbdPort, Col7Lo);
	ScanCode = CM3_inport(KbdPort);
	ScanCode |= 0x0F;
	ScanCode &= Col7Lo;
	if (ScanCode != Col7Lo)
	{
		return ProcKey();
	}

	CM3_outport(KbdPort, Col6Lo);
	ScanCode = CM3_inport(KbdPort);
	ScanCode |= 0x0F;
	ScanCode &= Col6Lo;
	if (ScanCode != Col6Lo)
	{
		return ProcKey();
	}

	CM3_outport(KbdPort, Col5Lo);
	ScanCode = CM3_inport(KbdPort);
	ScanCode |= 0x0F;
	ScanCode &= Col5Lo;
	if (ScanCode != Col5Lo)
	{
		return ProcKey();
	}

	CM3_outport(KbdPort, Col4Lo);
	ScanCode = CM3_inport(KbdPort);
	ScanCode |= 0x0F;
	ScanCode &= Col4Lo;
	if (ScanCode != Col4Lo)
	{
		return ProcKey();
	}

	return 0xFF;
}

unsigned char ProcKey()
{
	unsigned char j;
	for (j = 0; j <= 12; j++)
		if (ScanCode == ScanTable[j])
		{
			if (j > 9)
			{
				j = j + 0x37;
			}
			else
			{
				j = j + 0x30;
			}
			return j;
		}

	if (j == 12)
	{
		return 0xFF;
	}

	return (0);
}
