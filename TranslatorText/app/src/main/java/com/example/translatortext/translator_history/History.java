package com.example.translatortext.translator_history;

public class History {
    private int id;
    private String inputValue;
    private String mode;
    private String outputValue;

    public History(){

    }

    public History(int id, String inputValue, String mode, String outputValue){
       this.id=id;
       this.inputValue=inputValue;
       this.mode=mode;
       this.outputValue=outputValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    public String getMode() {return mode;}

    public void setMode(String mode) {this.mode = mode;}
}
