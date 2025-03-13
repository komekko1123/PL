#include <iostream>
#include <map>
#include <vector>

using namespace std;  // 簡化 std::map, std::vector, std::string

class SList {
public:
    enum Type { SYMBOL, NUMBER, LIST, PROC, LAMBDA };
    Type type;
    string value;
    vector<SList> list;
    SList (*proc)(vector<SList>);  

    
    // 建構子
    SList() : type(SYMBOL), value(""), proc(nullptr) {}
    SList(string val) : type(SYMBOL), value(val), proc(nullptr) {}
    SList(double num) : type(NUMBER), value(to_string(num)), proc(nullptr) {}
    SList(vector<SList> lst) : type(LIST), list(lst), proc(nullptr) {}
    SList(SList (*fn)(vector<SList>)) : type(PROC), proc(fn) {}

    // 取得值
    string val() const { return value; }
    vector<SList> getList() const { return list; }
    Type getType() const { return type; }

    // 執行函數
    SList call(vector<SList> args) const {
        if (type == PROC && proc) return proc(args);
        return SList("ERROR: Not a function");
    }
};

// 定義 Environment 類別
class Environment {
public:
    map<string, SList> env;

    // 查找變數或函數
    map<string, SList>* find(const string& var) {
        if (env.find(var) != env.end()) return &env;
        return nullptr;
    }
};

// 數學函數實作
  SList add(vector<SList> &args) {
      double sum = 0;
      for (auto& arg : args) sum += stod(arg.val());
      return SList(sum);
  }

SList subtract(vector<SList> args) {
    if (args.size() < 1) return SList("ERROR: Too few arguments");
    double result = stod(args[0].val());
    for (size_t i = 1; i < args.size(); i++) result -= stod(args[i].val());
    return SList(result);
}

SList multiply(vector<SList> args) {
    double result = 1;
    for (auto& arg : args) result *= stod(arg.val());
    return SList(result);
}

SList divide(vector<SList> args) {
    if (args.size() < 2) return SList("ERROR: Too few arguments");
    double result = stod(args[0].val());
    for (size_t i = 1; i < args.size(); i++) {
        if (stod(args[i].val()) == 0) return SList("ERROR: Division by zero");
        result /= stod(args[i].val());
    }
    return SList(result);
}

// 設定環境
void env_setup(Environment* std_env) {
    std_env->env["+"] = SList(&add);
    std_env->env["-"] = SList(&subtract);
    std_env->env["*"] = SList(&multiply);
    std_env->env["/"] = SList(&divide);
}

// 評估 SList 表達式
SList evaluate(SList s, Environment* env) {
    if (s.getType() == SList::SYMBOL) {
        return (*(env->find(s.val())))[s.val()];
    } else if (s.getType() == SList::NUMBER) {
        return s;
    } else if (s.getList().size() == 0) {
        return s;
    } else if (s.getList()[0].val() == "define") {
        return (env->env[s.getList()[1].val()] = evaluate(s.getList()[2], env));
    } else if (s.getList()[0].val() == "lambda") {
        s.type = SList::LAMBDA;
        return s;
    } else {  // 函數呼叫
        SList p = evaluate(s.getList()[0], env);
        vector<SList> args;
        for (size_t i = 1; i < s.getList().size(); i++) {
            args.push_back(evaluate(s.getList()[i], env));
        }
        if (p.getType() == SList::PROC) {
            return p.call(args);  // 執行函數
        } else {
            return evaluate(p, env);
        }
    }
}

// 測試函數
int main() {
    Environment env;
    env_setup(&env);

    // 設定變數
    evaluate(SList(vector<SList>{SList("define"), SList("x"), SList(10)}), &env);
    cout << "x = " << evaluate(SList("x"), &env).val() << endl;

    // 測試加法
    SList result = evaluate(SList({SList("+"), SList(5), SList(3)}), &env);
    cout << "5 + 3 = " << result.val() << endl;

    // 測試減法
    result = evaluate(SList({SList("-"), SList(10), SList(4)}), &env);
    cout << "10 - 4 = " << result.val() << endl;

    // 測試乘法
    result = evaluate(SList({SList("*"), SList(6), SList(7)}), &env);
    cout << "6 * 7 = " << result.val() << endl;

    // 測試除法
    result = evaluate(SList({SList("/"), SList(20), SList(5)}), &env);
    cout << "20 / 5 = " << result.val() << endl;

    return 0;
}