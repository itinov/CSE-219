package ui;

import Algorithm.AlgorithmType;
import Algorithm.ClusteringAlgorithm;
import Algorithm.Configuration;
import actions.AppActions;
import dataProcessors.AppData;
import dataProcessors.Data;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author Weixin Tan
 */
public class AppUI extends UITemplate {
    private ApplicationTemplate applicationTemplate;

    private Button                              scrnShootButton;
    private Button                              display;
    private Button                              cancel;
    private Button                              next;
    private LineChart<Number,Number>            chart;
    private TextArea                            textArea;
    private Label                               InfoText;
    private Label                               RunInfo;
    private AlgorithmType                       selectedAlgorithm;
    private ToggleButton                        edit;
    private ToggleButton                        complete;
    private Pane                                selectionPane;
    private Pane                                leftTopPane;
    private Set<String>                         algorithmTypeSet;
    private String                              configIconPath;
    private String                              backIconPath;
    private String                              startIconPath;
    private HashMap<String,Configuration>       configurationStoreMap;


    public Pane getLeftTopPane(){return leftTopPane;}

    public Button getNext() {
        return next;
    }

    public Button getCancel() {
        return cancel;
    }

    public Pane getSelectionPane() {
        return selectionPane;
    }

    public void setRunInfo(int currentInterval, int MaxIteration){
        RunInfo.setText(String.format(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.RUN_INFO_LABEL_TEXT.name()),
                currentInterval,MaxIteration));
    }

    public void disableToggleButtons(boolean disable, boolean editSelect, boolean completeSelect){
        edit.setDisable(disable);
        complete.setDisable(disable);

        edit.setSelected(editSelect);
        complete.setSelected(completeSelect);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
        configurationStoreMap=new HashMap<>();
        algorithmTypeSet= new HashSet<>();
        algorithmTypeSet.add(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION_TYPE.name()));
        algorithmTypeSet.add(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUSTERING_TYPE.name()));
    }

    //for Test Purpose
    public AppUI(){

    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager=applicationTemplate.manager;
        String iconPath = manager.getPropertyValue(AppPropertyTypes.GUI_ICON_PATH.name());
        configIconPath= iconPath +manager.getPropertyValue(AppPropertyTypes.CONFIGURATION_ICON.name());
        backIconPath= iconPath + manager.getPropertyValue(AppPropertyTypes.BACK_ICON.name());
        startIconPath= iconPath + manager.getPropertyValue(AppPropertyTypes.START_ICON.name());
    }
    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        PropertyManager manager = applicationTemplate.manager;
        super.setToolBar(applicationTemplate);
        String scrnShotPath =manager.getPropertyValue(AppPropertyTypes.Separator.name())+String.join(manager.getPropertyValue(AppPropertyTypes.Separator.name()),
                manager.getPropertyValue(PropertyTypes.GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(PropertyTypes.ICONS_RESOURCE_PATH.name()),
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnShootButton = setToolbarButton(scrnShotPath,manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),true);
        toolBar = new ToolBar(newButton,saveButton,loadButton,exitButton,scrnShootButton);
        newButton.setDisable(false);
    }
    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e ->  applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        scrnShootButton.setOnAction(e-> ((AppActions)applicationTemplate.getActionComponent()).handleScreenShootRequest());
    }
    @Override
    public void initialize(){
        layout();
        setWorkSpaceActions();
    }

    private void ToggleSwitchActions(ToggleButton edit, ToggleButton complete){
        ToggleGroup toggleSwitch= new ToggleGroup();
        edit.setToggleGroup(toggleSwitch);
        complete.setToggleGroup(toggleSwitch);
        edit.setOnAction((ActionEvent e) ->{
            edit.setSelected(true);
            textArea.setDisable(false);
            clearDataInofrmation();
            clearSelectionPane();
        });
        complete.setOnAction(e-> {
            if(((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText().isEmpty()){
                Dialog error= applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                error.show(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.INVALID_INPUT_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.EMPTY_TEXTBOX_MESSAGE.name()));
                edit.setSelected(true);
            }else {
                complete.setSelected(true);

                if (((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText())) {
                    textArea.setDisable(true);
                    clearDataInofrmation();
                    showDataInformation(((AppData) applicationTemplate.getDataComponent()).getOriginalData().getDataInfo(
                            applicationTemplate.manager.getPropertyValue(AppPropertyTypes.LOADED_DATA_INTO_FROM_TEXTBOX.name())));
                } else {
                    edit.setSelected(true);
                }
            }
        });
    }

    private void layout(){
        PropertyManager manager = applicationTemplate.manager;

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setForceZeroInRange(false);
        yAxis.setForceZeroInRange(false);

        chart= new LineChart<>(xAxis,yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));

        InfoText = new Label();
        InfoText.setWrapText(true);
        RunInfo = new Label();
        RunInfo.setWrapText(true);

        textArea = new TextArea();

        display = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(startIconPath))));
        display.setVisible(false);

        edit= new ToggleButton(manager.getPropertyValue(AppPropertyTypes.EDIT_BUTTON_LABEL.name()));
        edit.setPrefWidth(windowWidth*0.29*.3);
        complete = new ToggleButton(manager.getPropertyValue(AppPropertyTypes.COMPLETE_BUTTON_LABEL.name()));
        complete.setPrefWidth(windowWidth*0.29*.3);

        HBox ToggleSwitch = new HBox(edit,complete);
        ToggleSwitch.setAlignment(Pos.TOP_LEFT);
        ToggleSwitchActions(edit,complete);

        selectionPane=new VBox(10);
        selectionPane.setPadding(new Insets(10,10,10,10));
        selectionPane.setVisible(false);
        selectionPane.setVisible(false);
        VBox.setVgrow(selectionPane,Priority.ALWAYS);

        leftTopPane = new VBox(10);
        leftTopPane.getChildren().addAll(textArea,ToggleSwitch,InfoText);
        leftTopPane.setVisible(false);

        VBox leftPanel= new VBox(10);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setMaxWidth(windowWidth * 0.29);
        leftPanel.setMinWidth(windowWidth * 0.29);
        VBox.setVgrow(leftPanel,Priority.ALWAYS);

        leftPanel.getChildren().addAll(leftTopPane,selectionPane);
        VBox.setVgrow(leftTopPane,Priority.ALWAYS);

        VBox rightPanel = new VBox(chart);
        rightPanel.setMaxSize(windowWidth*.69,windowHeight*.69);
        rightPanel.setMinSize(windowWidth*.69,windowHeight*.69);
        VBox.setVgrow(chart,Priority.ALWAYS);

        workspace= new HBox(leftPanel,rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);
        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane,Priority.ALWAYS);
        primaryScene.getStylesheets().add(getClass().getResource(manager.getPropertyValue(AppPropertyTypes.CSS_Path.name())).toExternalForm());
    }

    private void setWorkSpaceActions(){
        textArea.textProperty().addListener((observable, oldValue, newValue)->{
            if(textArea.getText().isEmpty()) {
                saveButton.setDisable(true);
                newButton.setDisable(true);
            }
            else {
                if(((AppData)applicationTemplate.getDataComponent()).hasNewValidText(newValue))
                    saveButton.setDisable(false);
                else
                    saveButton.setDisable(true);
                newButton.setDisable(false);
            }
        });
        display.setOnAction(e-> ((AppData) applicationTemplate.getDataComponent()).loadDataToChart(selectedAlgorithm)
        );
    }

    public void showDataInformation(String dataInfo){InfoText.setText(dataInfo);}
    public void clearDataInofrmation(){
        InfoText.setText(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()));
    }
    public void disableNewButton(boolean disable){
        newButton.setDisable(disable);
    }
    public void disableSaveButton(boolean disable){
        saveButton.setDisable(disable);
    }
    public void disableScrnShotButton(boolean disable){
        scrnShootButton.setDisable(disable);
    }

    @Override
    public void clear(){
        clearChart();
        clearTextArea();
    }

    private void clearTextArea(){textArea.clear();}
    public TextArea getTextArea(){return textArea;}

    public void clearChart(){
        while(!chart.getData().isEmpty())
            chart.getData().remove((int)(Math.random()*(chart.getData().size()-1)));
        scrnShootButton.setDisable(true);
    }

    public void showDisplayPane(boolean continous){
        clearSelectionPane();
        AppData appData = (AppData)applicationTemplate.getDataComponent();
        next = new Button(applicationTemplate.manager.
                getPropertyValue(AppPropertyTypes.NEXT_RUN_BUTTON_LABEL.name()));
        cancel= new Button(applicationTemplate.manager.
                getPropertyValue(AppPropertyTypes.ALGORITHM_CANCEL_BUTTON_LABEL.name()));

        next.setVisible(!continous);

        HBox buttonPane = new HBox(cancel,next);

        Label Run_Info = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.RUN_INFO_LABEL.name()));
        Run_Info.setId(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.RUN_INFO_LABEL_ID.name()));

        selectionPane.getChildren().addAll(Run_Info,RunInfo,buttonPane);

        next.setOnAction((ActionEvent e) -> ((AppData)applicationTemplate.getDataComponent()).resume());

        cancel.setOnAction((ActionEvent e) ->{
            //TODO suspense thread when dialog is on
            if(cancel.getText()
                    .equals(PropertyManager.getManager().getPropertyValue(AppPropertyTypes.COMPLETE_BUTTON_LABEL.name()))){
                appData.cancel();
                showAlgorithmTypeSelection(appData.getOriginalData());
                clearChart();
            }else {
                ConfirmationDialog dialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
                dialog.show(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_CANCEL_WARNING_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_CANCEL_WARNING_MESSAGE.name()));
                if (dialog.getSelectedOption() == ConfirmationDialog.Option.YES) {
                    appData.cancel();
                    showAlgorithmTypeSelection(appData.getOriginalData());
                    clearChart();
                }
            }
        });
    }

    private void clearSelectionPane(){
        selectionPane.getChildren().clear();
        selectedAlgorithm=null;
    }

    public void showAlgorithmTypeSelection(Data data){
        clearSelectionPane();
        Label selectionLabel =
                new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()));
        selectionPane.getChildren().add(selectionLabel);
        for (String algorithmType : algorithmTypeSet) {
            RadioButton algorithm = new RadioButton(algorithmType);

            algorithm.setWrapText(true);
            algorithm.setMinWidth(windowWidth * 0.29-30);

            if(algorithmType.equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION_TYPE.name()))){
                if(data.getLabelNumber()==2)
                    selectionPane.getChildren().add(algorithm);
            }else
                selectionPane.getChildren().add(algorithm);
            algorithm.setOnAction((ActionEvent e) -> {
                display.setVisible(false);
                showAlgorithmSelection(algorithmType,data);
            });
        }
        selectionPane.setVisible(true);
    }

    private void showAlgorithmSelection(String algorithmType, Data data){
        clearSelectionPane();
        selectionPane.getChildren().add(
                new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHMS.name())));
        ToggleGroup group = new ToggleGroup();
        try{
            Class<?> klass= Class.forName(applicationTemplate.manager.getPropertyValue(algorithmType));

            for(Object algorithmName: klass.getDeclaredClasses()[0].getEnumConstants()){
                {
                    Class algorithmKlass =Class.forName(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.Algorithm.name())+algorithmName);
                    Constructor algorithmKonstructor =algorithmKlass.getConstructors()[0];

                    HBox algorithmsAndConfiguration = new HBox();
                    HBox.setHgrow(algorithmsAndConfiguration,Priority.ALWAYS);

                    RadioButton algorithmButton = new RadioButton(applicationTemplate.manager.getPropertyValue(algorithmName.toString()));

                    algorithmButton.setWrapText(true);
                    algorithmButton.setToggleGroup(group);
                    algorithmButton.setTooltip(
                            new Tooltip(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONFIGURATION_TOOLTIP.name())));

                    Button config =
                            new Button(null, new ImageView(new Image(getClass().getResourceAsStream(configIconPath))));
                    config.setId(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ConfigurationButton_ID.name()));


                    AlgorithmType algorithm = (AlgorithmType) algorithmKonstructor.newInstance(data);
                    if(configurationStoreMap.containsKey(algorithm.getClass().getName())){
                        algorithm.getConfiguration().setConfigration(configurationStoreMap.get(algorithm.getClass().getName()));
                    }

                    algorithmsAndConfiguration.setMinWidth(windowWidth * 0.29 - 30);
                    algorithmsAndConfiguration.getChildren().addAll(algorithmButton,config);


                    selectionPane.getChildren().add(algorithmsAndConfiguration);
                    algorithmButton.setOnAction(e->{
                        selectedAlgorithm=algorithm;
                        if(checkInitConfiguration(selectedAlgorithm))
                            display.setVisible(true);
                        else
                            display.setVisible(false);
                    });
                    config.setOnAction(e->initConfiguration(primaryStage,algorithm));
                }
            }
        }catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            //Nothing
        }
        group.getSelectedToggle();

        Button back =new Button(null, new ImageView(new Image(getClass().getResourceAsStream(backIconPath))));
        HBox buttonPane = new HBox(back,display);
        buttonPane.setAlignment(Pos.CENTER_LEFT);
        buttonPane.setSpacing(4);
        HBox.setHgrow(back,Priority.ALWAYS);
        HBox.setHgrow(display,Priority.ALWAYS);

        selectionPane.getChildren().addAll(buttonPane);
        back.setOnAction(e->showAlgorithmTypeSelection(data));
    }

    private boolean checkInitConfiguration(AlgorithmType algorithmType){
        Configuration configuration =algorithmType.getConfiguration();
        boolean isSet= configuration.IterationInterval>0&&
                        configuration.MaxInterval>0;
        if(algorithmType.getClass().getSuperclass().equals(ClusteringAlgorithm.class))
            return isSet && configuration.NumberOfClustering>0;
        else
            return isSet;

    }

    private void initConfiguration(Stage owner, AlgorithmType algorithmType){
        Stage configurationStage = new Stage();

        configurationStage.initModality(Modality.WINDOW_MODAL);
        configurationStage.initOwner(owner);

        PropertyManager manager = applicationTemplate.manager;

        CheckBox continous =
                new CheckBox(manager.getPropertyValue(AppPropertyTypes.CONTINUOUS_RUN_TEXT.name()));
        if(algorithmType.getConfiguration().continous)
            continous.setSelected(true);
        else
            continous.setSelected(false);
        VBox configurationPanel= new VBox();
        TextField maxIntervalInput = new TextField(Integer.toString(algorithmType.getConfiguration().MaxInterval));
        TextField iterationInterval = new TextField(Integer.toString(algorithmType.getConfiguration().IterationInterval));
        TextField NumberofClusters = new TextField(Integer.toString(algorithmType.getConfiguration().NumberOfClustering));
        Button setButton =
                new Button(manager.getPropertyValue(AppPropertyTypes.CONFIRM_TEXT.name()));
        Button cancelButton =
                new Button(manager.getPropertyValue(AppPropertyTypes.CANCEL_TEXT.name()));

        HBox buttonsPane = new HBox(setButton,cancelButton);
        buttonsPane.setAlignment(Pos.CENTER);
        buttonsPane.setSpacing(5);
        configurationPanel.getChildren().addAll(
                new Label(manager.getPropertyValue(AppPropertyTypes.MAX_ITERATION_TEXT.name())),
                maxIntervalInput,
                new Label(manager.getPropertyValue(AppPropertyTypes.ITERATION_INTERVAL_TEXT.name())),
                iterationInterval);
        configurationPanel.setAlignment(Pos.TOP_LEFT);
        configurationPanel.setSpacing(10);
        configurationPanel.setPadding(new Insets(10,10,10,10));
        if(algorithmType.getClass().getSuperclass().equals(ClusteringAlgorithm.class)){
            configurationPanel.getChildren().addAll(
                            new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.NUMBER_OF_CLUSTER.name())
                            ),NumberofClusters);
        }
        configurationPanel.getChildren().addAll(continous,buttonsPane);
        Scene configurationScene = new Scene(configurationPanel);
        configurationStage.setScene(configurationScene);
        configurationStage.show();
        cancelButton.setOnAction((e->configurationStage.close()));
        setButton.setOnAction(e->{
            ConfigurationAction(algorithmType, maxIntervalInput.getText(), iterationInterval.getText(),
                        continous.isSelected(), configurationStage, NumberofClusters.getText());
            try {
                if (checkInitConfiguration(selectedAlgorithm))
                    display.setVisible(true);
                else
                    display.setVisible(false);
            }catch (NullPointerException error) {
                //do nothing
            }
        });
    }

    //if user enters invalid number, it will automatically set to default of 1
    private void ConfigurationAction(AlgorithmType algorithmType,
                                     String maxInterval,
                                     String IterationInterval,
                                     Boolean iscontinousRun,
                                     Stage configurationStage, String NumberOfClustering){
        try{
            Integer maxInt = new Integer(maxInterval);
            if(maxInt<0)
                maxInt=1;
            else if (maxInt==0)
                maxInt=algorithmType.getConfiguration().MaxInterval;

            Integer iterationInt = new Integer(IterationInterval);
            if(iterationInt<0)
                iterationInt=1;
            else if(iterationInt>maxInt)
                iterationInt=maxInt;
            else if(iterationInt==0)
                iterationInt=algorithmType.getConfiguration().IterationInterval;

            Integer numberofCluster =  new Integer(NumberOfClustering);
            if(numberofCluster<0)
                numberofCluster=2;
            else if(numberofCluster>4)
                numberofCluster=4;
            if(algorithmType.getClass().getSuperclass().equals(ClusteringAlgorithm.class))
                algorithmType.getConfiguration().NumberOfClustering =numberofCluster;

            algorithmType.getConfiguration().continous=iscontinousRun;
            algorithmType.getConfiguration().MaxInterval=maxInt;
            algorithmType.getConfiguration().IterationInterval=iterationInt;
            if(configurationStage!=null) {
                configurationStoreMap.put(algorithmType.getClass().getName(), algorithmType.getConfiguration());
                configurationStage.close();
            }
        }catch (NumberFormatException error){
            //do nothing
        }
    }
}
