package dataProcessors;

import org.junit.Before;
import org.junit.Test;
import vilij.templates.ApplicationTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author weixin
 */
public class ReadLineToDataTest {
    private AppData appData;
    private Method CheckDataValidityMethod;
    private ApplicationTemplate applicationTemplate;

    /**
     * Test the whole CheckDataValidityMethod of AppData class, which process the string to data.
     */
    @Before
    public void setUpClass(){
        appData= new AppData(applicationTemplate);
        for(Method m:appData.getClass().getDeclaredMethods() ){
            if(m.getName().equals("CheckDataValidity")) {
                CheckDataValidityMethod = m;
                break;
            }
        }
        CheckDataValidityMethod.setAccessible(true);
    }
    /**
     * Cases that are suppose to go through
     */
    //single valid line
    @Test
    public void CheckDataValidityTest_Pass() throws IllegalAccessException, InvocationTargetException{
        String TestString_Valid="@instance1\tlabel1\t4.5,3.5\n";
        CheckDataValidityMethod.invoke(appData,TestString_Valid);
    }

    //single valid line with "null" as its label
    @Test
    public void CheckDataValidityTest_Pass_NullLabel()throws IllegalAccessException,InvocationTargetException{
        String TestString_NullLabel="@instance1\tnull\t4.5,3.5\n";
        CheckDataValidityMethod.invoke(appData,TestString_NullLabel);
    }

    /**
     * Cases that are not suppose to go through
     */
    //3 lines with line 1 and line 3 having same instance name.
    @Test
    public void CheckDataValidityTest__Fail_RepeatingID(){
        String TestString_RepeatingID="@instance1\tlabel1\t4.5,3.5\n" +
                "@instance2\tlabel2\t5.3,3.5\n"+
                "@instance1\tlabel3\t6.5,5.5\n";
        try {
            CheckDataValidityMethod.invoke(appData, TestString_RepeatingID);
        }catch (Exception e){
            String expectedErrorMessage ="Instance name '@instance1' cannot be use at line 3 because it already exist. " +
                    "All data instance names must be use only once. ";
            Throwable actualException = e.getCause();
            assertEquals(expectedErrorMessage,actualException.getMessage());
        }
    }

    //string pass to CheckDataValidityMethod is null
    @Test (expected = NullPointerException.class)
    public void CheckDataValidityTest__Fail_NullString() throws Throwable{
        try {
            CheckDataValidityMethod.invoke(appData, (Object) null);
        }catch (Exception e){
            throw e.getCause();
        }
    }

    //empty string input or file
    @Test
    public void CheckDataValidityTest__Fail_EmptyString(){
        String TestingString_Empty="";
        try {
            CheckDataValidityMethod.invoke(appData, TestingString_Empty);
        }catch (Exception e){
            String expectedErrorMessage="Invalid name '' at line 1. All data instance names must start with the @ character. ";
            assertEquals(expectedErrorMessage,e.getCause().getMessage());
        }
    }

    //unvalid string format
    @Test
    public void CheckDataValidityTest_Fail_InvalidString(){
        String TestingString_Invalid="@instance1,label1";
        try{
            CheckDataValidityMethod.invoke(appData,TestingString_Invalid);
        }catch (Exception e){
            String expectedErrorMessage="Invalid data format ar line 1.";
            assertEquals(expectedErrorMessage,e.getCause().getMessage());
        }
    }

    //unvalid point format ex.(2.2.2,3.2)
    @Test
    public void CheckDataValidity_Fail_InvalidPoint(){
        String TestingString_InvalidPoint="@instance1\tlabel1\t2.2.2,3.2\n";
        try{
            CheckDataValidityMethod.invoke(appData,TestingString_InvalidPoint);
        }catch (Exception e){
            String expectedErrorMessage="Invalid data format ar line 1.";
            assertEquals(expectedErrorMessage,e.getCause().getMessage());
        }
    }
}