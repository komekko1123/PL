#include <iostream>
#include <vector>
#include <string>
#include <map>
using namespace std;

class token {
    string content;
public:
    token(){}
    token(string s) : content(s) {}
    string val() { return content; }
};

int main() {
    vector<token> params = { token("x"), token("y"), token("z") };
    vector<token> args = { token("10"), token("20"), token("30") };

    map<string, token> env;

    auto a = args.begin();
    for (auto vi = params.begin(); vi != params.end() && a != args.end(); vi++) {
        env[vi->val()] = *a;
        a++;
    }

    for (auto it = env.begin(); it != env.end(); it++) {
        cout << it->first << " => " << it->second.val() << endl;
    }

    return 0;
}