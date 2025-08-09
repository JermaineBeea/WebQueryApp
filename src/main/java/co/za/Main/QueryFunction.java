package co.za.Main;

public class QueryFunction {

    Long variableA;
    Long variableB;
    Long variableC;

    public QueryFunction(Long argA, Long argB, Long argC){
        this.variableA = argA;
        this.variableB = argB;
        this.variableC = argC;
    }
    
    // General function. a = b + c
    public Long returnA(){
        return variableB + variableC;
    }

    public Long returnB(){
        return variableA - variableC;
    }

    public Long returnC(){
        return variableA - variableB;
    }
}
