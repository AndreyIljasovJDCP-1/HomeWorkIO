package shopping;

public class Config {

    private boolean loadEnabled;
    private boolean saveEnabled;
    private boolean logEnabled;
    private String loadFileName;
    private String saveFileName;

    private String loadFileExtension;
    private String saveFileExtension;

    private  String logFileName;

    public Config() {

    }

    public boolean isLoadEnabled() {
        return loadEnabled;
    }

    public void setLoadEnabled(String xmlLoadEnabled) {
        this.loadEnabled = "true".equals(xmlLoadEnabled);
    }

    public boolean isSaveEnabled() {
        return saveEnabled;
    }

    public void setSaveEnabled(String xmlSaveEnabled) {
        this.saveEnabled = "true".equals(xmlSaveEnabled);
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(String xmlLogEnabled) {
        this.logEnabled = "true".equals(xmlLogEnabled);
    }

    public String getLoadFileName() {
        return "basket_repo/" + loadFileName;
    }

    public void setLoadFileName(String loadFileName) {
        this.loadFileName = loadFileName;
    }

    public String getSaveFileName() {
        return "basket_repo/" + saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public String getLoadFileExtension() {
        return loadFileExtension;
    }

    public void setLoadFileExtension(String loadFileExtension) {
        this.loadFileExtension = loadFileExtension;
    }

    public String getSaveFileExtension() {
        return saveFileExtension;
    }

    public String getLogFileName() {
        return "basket_repo/" + logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void setSaveFileExtension(String saveFileExtension) {
        this.saveFileExtension = saveFileExtension;
    }
}
