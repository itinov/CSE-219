package dataProcessors;

import Algorithm.AlgorithmType;
import Algorithm.ClassificationAlgorithm;
import Algorithm.ClusteringAlgorithm;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
/**
 *
 * @author Weixin Tan
 */
public class AppData implements DataComponent {

    private static final String NEW_LINE_CHAR ="\n";
    private final ApplicationTemplate applicationTemplate;
    private DataProcessor processor;
    private Data originalData;
    private String originallData;

    private String initialSaveText;

    public AppData(ApplicationTemplate applicationTemplate){
        this.applicationTemplate=applicationTemplate;
    }

    public Data getOriginalData() {
        try {
            CheckDataValidity(originallData);
        }catch (Exception e){
            //Do nothing
        }
        return originalData;
    }

    public DataProcessor getProcessor() {
        return processor;
    }

    @Override
    public void loadData(Path dataFilePath){
        StringBuilder stringbuilder = new StringBuilder();
        PropertyManager manager = applicationTemplate.manager;
        AppUI ui= (AppUI)applicationTemplate.getUIComponent();
        Dialog error = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        if(dataFilePath.toString().endsWith(manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name())
                .substring(1))){
            try{
                String temp;
                BufferedReader fileReader = new BufferedReader(new FileReader(dataFilePath.toFile()));
                while((temp=fileReader.readLine())!=null){
                    stringbuilder.append(temp);
                    stringbuilder.append(NEW_LINE_CHAR);
                }
                fileReader.close();
                CheckDataValidity(stringbuilder.toString());
                ui.getTextArea().setText(originalData.getFirstTenLines());
                ui.showDataInformation(
                        originalData.getDataInfo(manager.getPropertyValue(AppPropertyTypes.LOADED_DATA_INFO_TEXT.name()),
                                manager.getPropertyValue(AppPropertyTypes.LOADED_FILE_LOCATION_TEXT.name()),
                                dataFilePath.getFileName().toString(),dataFilePath.toAbsolutePath().toString()));
                ui.getTextArea().setDisable(true);
                if(!ui.getLeftTopPane().isVisible())
                    ui.getLeftTopPane().setVisible(true);
                ui.showAlgorithmTypeSelection(originalData);
                ui.disableSaveButton(true);
                ui.disableToggleButtons(true, false,true);
            }catch (IOException io){
                error.show(manager.getPropertyValue(AppPropertyTypes.LOAD_ERROR_TITLE.name()),
                        manager.getPropertyValue(AppPropertyTypes.LOAD_IO_ERROR_MESSAGE.name()));
            }catch (Exception e){
                error.show(manager.getPropertyValue(AppPropertyTypes.LOAD_ERROR_TITLE.name()),
                        e.getMessage());
            }
        }else{
            //incorrect data format error
            String filePath = dataFilePath.toString();
            String fileExtension = filePath.substring(filePath.lastIndexOf('.')+1);
            error.show(manager.getPropertyValue(AppPropertyTypes.LOAD_ERROR_TITLE.name()),
                    String.format(manager.getPropertyValue(AppPropertyTypes.LOAD_WRONG_FORMAT_MESSAGE.name())
                            , fileExtension));
        }
    }

    public void loadDataToChart(AlgorithmType algorithmType){
        if(algorithmType.getClass().getSuperclass().equals(ClusteringAlgorithm.class)){
            processor= new ClusteringProcessor(algorithmType,
                    ((AppUI)applicationTemplate.getUIComponent()).getChart(),originalData,applicationTemplate);
        }else if(algorithmType.getClass().getSuperclass().equals(ClassificationAlgorithm.class)){
            processor=new ClassificationProcessor((ClassificationAlgorithm) algorithmType,
                    ((AppUI)applicationTemplate.getUIComponent()).getChart(),
                    applicationTemplate,getOriginalData().getMaxX(),getOriginalData().getMinX(),originalData);
        }

        ((AppUI)applicationTemplate.getUIComponent()).clearChart();
        processor.toChartData(originalData,((AppUI)applicationTemplate.getUIComponent()).getChart());
        if(processor.getClass().getName().contains(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.CLASSIFICATION_PROCESSOR.name())))
            ((ClassificationProcessor)processor).setChartOriginalDataSize();
        try {
            for (Method m : processor.getClass().getMethods()) {
                if (m.getName().equals(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.START.name()))) {
                    m.invoke(processor);
                    break;
                }

            }
        }catch (IllegalAccessException |InvocationTargetException e){
            //Do nothing
        }
    }

    @Override
    public void saveData(Path dataFilePath){
        Dialog error = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager= applicationTemplate.manager;
        AppUI ui= (AppUI) applicationTemplate.getUIComponent();
        try{
            CheckDataValidity(((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText());
            if(null != dataFilePath.toFile()){
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFilePath.toFile()));
                bufferedWriter.write(originalData.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
            }
            initialSaveText=originalData.toString();
            Dialog dialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dialog.show(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SAVE_TITLE.name()),
                    String.join("\n",String.format(applicationTemplate.manager
                                    .getPropertyValue(AppPropertyTypes.SAVE_LOCATION_MESSAGE.name()),
                            dataFilePath.getFileName().toString()),dataFilePath.toFile().getAbsolutePath()));
            ui.disableSaveButton(true);
        }catch (IOException io){
            error.show(manager.getPropertyValue(AppPropertyTypes.SAVE_ERROR_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.SAVE_IO_ERROR_MESSAGE.name()));

        }catch (Exception e){
            error.show(manager.getPropertyValue(AppPropertyTypes.SAVE_ERROR_TITLE.name()),
                    e.getMessage());
        }
    }

    public boolean loadData(String data){
        PropertyManager manager = applicationTemplate.manager;
        try{
            CheckDataValidity(data);
            ((AppUI)applicationTemplate.getUIComponent()).showAlgorithmTypeSelection(originalData);
            return true;
        }catch (Exception e){
            Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialog.show(manager.getPropertyValue(AppPropertyTypes.INVALID_INPUT_TITLE.name()),
                    e.getMessage());
            return false;//failed to load Data
        }
    }
    public boolean hasNewValidText(String textAreaContent){
        Data temp = new Data();
        try {
            temp.setData(textAreaContent);
            return !temp.toString().equals(initialSaveText);

        }catch (Exception e){
            return false;//not valid text
        }
    }
    @Override
    public void clear() {
        originalData.clear();
    }
    //check if data is valid for TSD format saving
    private void CheckDataValidity(String data) throws Exception{
        originalData = new Data();
        originalData.setData(data);
        originallData=data;
    }


    public void resume(){
        synchronized (processor){
            processor.notify();
        }
    }
    public void cancel(){
        processor.terminate();
        processor=null;
    }
}
