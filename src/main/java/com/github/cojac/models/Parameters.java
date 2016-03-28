package com.github.cojac.models;

public enum Parameters {
    INTEGER_BINARY_PARAMS(int.class, int.class),
    INTEGER_UNARY_PARAMS(int.class),
    LONG_BINARY_PARAMS(long.class, long.class),
    LONG_UNARY_PARAMS(long.class),
    FLOAT_BINARY_PARAMS(float.class, float.class),
    FLOAT_UNARY_PARAMS(float.class),
    DOUBLE_BINARY_PARAMS(double.class, double.class),
    DOUBLE_UNARY_PARAMS(double.class);
   
    
    public Class<?>[] params;
    private Parameters(Class<?>... params) {
        this.params = params;
    }
}
