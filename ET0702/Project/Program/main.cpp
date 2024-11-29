#include <iostream>
#include <vector>
#include <fstream>
#include <string>
#include <algorithm>
#include <cmath>

using namespace std;

void visuals();
void permutation();
void set(vector<int> a, int n);
void display(vector<vector<int>> v);
string NumToOrd(size_t num);

vector<int> locx;
vector<int> locy;
vector<int> vec_sum;
vector<int> permutation_vec;
vector<int> seq;

int main()
{
    vector<int> locations;
    int count = 0, tmp, numx, numy;

    ifstream myfile("data.txt", ios::in);

    while (myfile >> tmp) {
        locations.push_back(tmp);
        count++;
    }

    for (int i = 0; i < locations.size(); i += 2) {
        numx = locations[i];
        locx.push_back(numx);
    }
    for (int x = 1; x < locations.size(); x += 2) {
        numy = locations[x];
        locy.push_back(numy);
    }

    permutation();

    const int column = locx.size();
    int row = 1;
    for (int i = 1; i <= column; ++i)
        row *= i;

    vector<vector<int>> permutation(row, vector<int>(column, 0));
    int k = 0;
    for (int i = 0; i < row; i++) {
        for (int j = 0; j < column; j++) {
            permutation[i][j] = permutation_vec[k++];
        }
    }

    for (int i = 0; i < row; i++) {
        int sum = 0, count_seq = 0, sum_0 = 0;
        for (int x = 0; x < column - 1; x++) {
            int temp = permutation[i][x] - 1;
            int temp2 = permutation[i][x + 1] - 1;
            int sum_temp = abs(locx[temp] - locx[temp2]) + abs(locy[temp] - locy[temp2]);
            sum += sum_temp;
            if (count_seq == 0) {
                sum_0 = abs(0 - locx[temp]) + abs(0 - locy[temp]);
            }
            count_seq++;
        }
        sum += sum_0;
        vec_sum.push_back(sum);
    }

    int minDistanceIndex = (min_element(vec_sum.begin(), vec_sum.end()) - vec_sum.begin());
    int minDistance = *min_element(vec_sum.begin(), vec_sum.end());

    for (int i = 0; i < column; i++) {
        int tempseq = permutation[minDistanceIndex][i];
        seq.push_back(tempseq);
    }

    cout << "Sequence: ";
    for (int i = 0; i < seq.size(); i++) {
        cout << seq[i];
        if (i < seq.size() - 1) {
            cout << ",";
        }
    }

    cout << "\n\n";
    visuals();
    cout << "Total distance moved: " << minDistance << endl;
}

void permutation() {
    int n = locx.size();
    vector<int> a(n);
    for (int i = 0; i < n; i++) {
        a[i] = (i + 1);
    }
    do {
        set(a, n);
    } while (next_permutation(a.begin(), a.end()));
}

void set(vector<int> a, int n) {
    for (int i = 0; i < n; i++) {
        int x = a[i];
        permutation_vec.push_back(x);
    }
}

void display(vector<vector<int>> v) {
    for (vector<int> visual1D : v) {
        for (int x : visual1D) {
            cout << (char)x << " ";
        }
        cout << endl;
    }
}

void visuals() {
    vector<vector<int>> visual(6, vector<int>(7, 'O'));

    visual[5][0] = 'H';

    for (int i = 0; i < locx.size(); i++) {
        char x = i + 1;
        visual[5 - locy[i]][0 + locx[i]] = 'X';
    }
    display(visual);
    cout << "(" << NumToOrd(1) << " display)" << endl;
    cout << "H - home (0,0)\n\n";

    for (int i = 0; i < locx.size(); i++) {
        int y = seq[i] - 1;
        visual[5 - locy[y]][0 + locx[y]] = (y + 49);
        display(visual);
        cout << "(" << NumToOrd(i + 2) << " display)" << endl;
        cout << "(" << locx[y] << "," << locy[y] << ") retrieved\n\n";
    }
}

string NumToOrd(size_t num) {
    string suffix = "th";
    if (num % 100 < 11 || num % 100 > 13) {
        switch (num % 10) {
        case 1:
            suffix = "st";
            break;
        case 2:
            suffix = "nd";
            break;
        case 3:
            suffix = "rd";
            break;
        }
    }
    return to_string(num) + suffix;
}