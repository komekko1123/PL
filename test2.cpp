#include <iostream>
#include <vector>
#include <map>
#include <string>
using namespace std;

class Test;
typedef Test (*FUNCALLPTR)(const vector<Test>&);


class Test {
public:
    int value;  
    FUNCALLPTR call;

    Test(){
      value =0;
      call = NULL;
    }
    Test(int v){
        value = v;
        call = NULL;    
    } 
    // function pointer
    Test (FUNCALLPTR f){
        value = 0;
        call = f;
    }

    void setValue(int value){
      this->value = value;
    } // setValue
};

Test testFunction(const vector<Test>& vec) {
    cout << "call testFunction，vector size()：" << vec.size() << endl;
    Test t;
    t.value = vec.size();
    return t;
}

class Environment {
public:
    map<string, Test> env;
    map<string, Test>* findT(string var) {
        if (env.find(var) != env.end()) 
            return &env;
        return NULL;
    }
};

int main() {
    Test t(123);
    t.call = testFunction;
    vector<Test> vec;
    vec.push_back(Test(1));
    vec.push_back(Test(2));
    vec.push_back(Test(3));
    cout << vec.size() << endl;
    vector<Test> vec2;
    vec2.push_back(Test(4));
    vec2.push_back(Test(5));
    vec2.push_back(Test(6));
    vec2.push_back(Test(4));
    vec2.push_back(Test(5));
    vec2.push_back(Test(6));
    cout << vec2.size() << endl;
    // FUNCTION POINTER
    Test result = t.call(vec);
    cout << "CALL THE FUNCTION AND RETURN VALUE IS ：" << result.value << endl;

    //ENV AS T
    Environment* env = new Environment();
    env->env["test"] = Test(testFunction);
    map<string, Test>* found = env->findT("test");
    if (found) {
        cout << "FIND IT AND VALUE：" << (*found)["test"].value << endl;
        Test result2 = (*found)["test"].call(vec2);
        cout << "call the same env but different size value：" << result2.value << endl;
    } else {
        cout << "did't found the 'test'" << endl;
    }

    return 0;
}