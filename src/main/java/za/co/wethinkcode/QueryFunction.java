package za.co.wethinkcode;

public class QueryFunction {

    int variableA;
    int variableB;
    int variableC;

    public QueryFunction(int argA, int argB, int argC){
        this.variableA = argA;
        this.variableB = argB;
        this.variableC = argC;
    }
    
    // General function. a = b + c
    public int functionA(){
        return variableB + variableC;
    }

    public int functionB(){
        return variableA - variableC;
    }

    public int functionC(){
        return variableA - variableB;
    }
}
