package settings;


/**
 *
 * @author Weixin Tan
 */
public enum AppPropertyTypes {
    /* path to the icons*/
    GUI_ICON_PATH,

    /*USER INTERFACE ICON FILES*/
    SCREENSHOT_ICON,
    CONFIGURATION_ICON,
    BACK_ICON,
    START_ICON,

    /*TOOLTIPS FOR BUTTONS*/
    SCREENSHOT_TOOLTIP,
    CONFIGURATION_TOOLTIP,

    /* file path symbol*/
    Separator,

    /* application GUI label*/
    ALGORITHM_TYPES,
    ALGORITHMS,
    CONTINUOUS_RUN_TEXT,
    CONFIRM_TEXT,
    CANCEL_TEXT,
    MAX_ITERATION_TEXT,
    ITERATION_INTERVAL_TEXT,
    NUMBER_OF_CLUSTER,
    LOADED_DATA_INFO_TEXT,
    LOADED_DATA_INTO_FROM_TEXTBOX,
    LOADED_FILE_LOCATION_TEXT,
    CHART_TITLE,
    EDIT_BUTTON_LABEL,
    COMPLETE_BUTTON_LABEL,
    RUN_INFO_LABEL,
    RUN_INFO_LABEL_TEXT,
    ALGORITHM_CANCEL_BUTTON_LABEL,
    NEXT_RUN_BUTTON_LABEL,
    NULL_LABEL,

    /* application parameter*/
    CSS_Path,
    CSS_LINE_ID,
    RUN_INFO_LABEL_ID,
    EMPTY_STRING,
    ConfigurationButton_ID,

    /*parameters for saving*/
    DATA_FILE_EXT_DESC,
    DATA_FILE_EXT,
    DATA_RESOURCE_PATH,
    Image_File_Ext,
    Image_File_Ext_Desc,
    Image_File_Ext_With_StarKey,

    /*algorithm types labels*/
    CLUSTERING_TYPE,
    CLASSIFICATION_TYPE,

    /*message titles*/
    SAVE_UNSAVED_WORK_TITLE,
    LOAD_ERROR_TITLE,
    SAVE_ERROR_TITLE,
    Subdir_Not_Found_Title,
    ScreenShot_Error_Title,
    INVALID_INPUT_TITLE,
    SAVE_TITLE,
    ALGORITHM_CANCEL_WARNING_TITLE,
    ALGORITHM_EXIT_WARNING_TITLE,
    ALGORITHM_RUNNING_TITLE,

    /*message contents*/
    SAVE_UNSAVED_WORK,
    LOAD_IO_ERROR_MESSAGE,
    LOAD_WRONG_FORMAT_MESSAGE,
    SAVE_IO_ERROR_MESSAGE,
    RESOURCE_SUBDIR_NOT_FOUND_MESSAGE,
    ScreenShot_Error_Message,
    SAVE_LOCATION_MESSAGE,
    EMPTY_TEXTBOX_MESSAGE,
    ALGORITHM_CANCEL_WARNING_MESSAGE,
    ALGORITHM_EXIT_WARNING_MESSAGE,
    UNSAVED_WORK_EXIT_MESSAGE,
    ALGORITHM_RUNNING_NEW_MESSAGE,
    ALGORITHM_RUNNING_LOAD_MESSAGE,

    /* Algorithm Type Name for Reflection*/
    Algorithm,

    /*classification names*/
    RANDOM_CLASSIFICATION,
    CLASSIFICATION_PROCESSOR,

    /*Method Name*/
    GETOUPUT,
    START,
}
