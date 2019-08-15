package ui;


import actions.AppActions;
import dataProcessors.AppData;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;
import static vilij.settings.InitializationParams.*;


/**
 *
 * @author Ivan Tinov
 */
public final class DataVisualizer extends ApplicationTemplate {

    @Override
    public void start(Stage primaryStage){
        dialogsAudit(primaryStage);
        if(propertyAudit())
            userInterfaceAudit(primaryStage);
    }

    @Override
    protected boolean propertyAudit(){
        boolean failed =(manager==null
                ||!(loadProperties(PROPERTIES_XML)&& loadProperties(WORKSPACE_PROPERTIES_XML)));
        if(failed)
            errorDialog.show(LOAD_ERROR_TITLE.getParameterName(),PROPERTIES_LOAD_ERROR_MESSAGE.getParameterName());
        return !failed;
    }

    @Override
    protected void userInterfaceAudit(Stage primaryStage) {
        setUIComponent(new AppUI(primaryStage, this));
        setActionComponent(new AppActions(this));
        setDataComponent(new AppData(this));
        uiComponent.initialize();
    }

    public static void main(String[] args) {
        launch();
    }

}
