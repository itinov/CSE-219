package dataProcessors;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SaveDataToFileTest {

    private Path ExistFile;
    private String textArea;


    /**
     * Test part of the saveData method of AppData class, which save the data to a tsd file.
     */
    @Before
    public void setUpClass(){
        String StringPath="./resources/data/sample-data.tsd";
        ExistFile= Paths.get(StringPath);
    }

    /**
     *
     * cases that suppose to save
     */

    //valid data to valid existing path
    @Test
    public void saveValidTextAreaToValidathTest() throws Exception{
        textArea="@instance1\tlabel1\t4.5,3.5\n";
        Data validData=new Data();
        validData.setData(textArea);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ExistFile.toFile()));
        bufferedWriter.write(validData.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }
    //save valid text area to non existing file but path is correct
    @Test
    public void saveValidDataToNewPathTest()throws Exception{
        textArea="@instance1\tlabel1\t4.5,3.5\n";
        Data validData=new Data();
        validData.setData(textArea);
        //no such file exist
        String PathString="./resources/data/noFile.tsd";
        Path newFile =Paths.get(PathString);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFile.toFile()));
        bufferedWriter.write(validData.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }


    /**
     * Cases that are not suppose to save
     */
    //invalid data(missing label name) to existing file
    @Test
    public void saveInvalidTextAreaToValidPathTest(){
        textArea="@instance1\t4.5,3.5\n";
        Data validData = new Data();
        try {
            validData.setData(textArea);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ExistFile.toFile()));
            bufferedWriter.write(validData.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (Exception e){
            String errorMessage="Invalid data format ar line 1.";
            assertEquals(errorMessage,e.getMessage());
        }
    }

    //save text area with repeating name into existing file
    @Test
    public void saveRepeatingNameTextAreaToValidPathTest(){
        textArea="@instance1\tlabel1\t4.5,3.5\n" +
                "@instance2\tlabel2\t5.3,3.5\n"+
                "@instance1\tlabel3\t6.5,5.5\n";
        try {
            Data validData = new Data();
            validData.setData(textArea);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ExistFile.toFile()));
            bufferedWriter.write(validData.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (Exception e){
            String errorMessage="Instance name '@instance1' cannot be use at line 3 because " +
                    "it already exist. All data instance names must be use only once. ";
            assertEquals(errorMessage,e.getMessage());
        }
    }

    //save empty text area to existing file
    @Test
    public void saveEmptyTextAreaToValidPathTest(){
        textArea="";
        try {
            Data validData = new Data();
            validData.setData(textArea);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ExistFile.toFile()));
            bufferedWriter.write(validData.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (Exception e){
            String errorMessage="Invalid name '' at line 1. All data instance names must start with " +
                    "the @ character. ";
            assertEquals(errorMessage,e.getMessage());
        }
    }


    //save valid text area to a wrong path
    @Test (expected = FileNotFoundException.class)
    public void saveValidDataToInvalidPAthTest() throws Exception{
        textArea="@instance1\tlabel1\t4.5,3.5\n";
        Data validData=new Data();
        validData.setData(textArea);
        //no such file exist
        String PathString="/user/kdjfkd/fdf/data/noFile.txt";
        Path InvalidFile =Paths.get(PathString);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(InvalidFile.toFile()));
        bufferedWriter.write(validData.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }


}