package shopping;

import java.io.Serializable;

public class Config implements Serializable {

    private boolean loadEnabled;
    private boolean saveEnabled;
    private boolean logEnabled;
    private String loadFileName;
    private String saveFileName;
    private String loadFileFormat;
    private String saveFileFormat;
    private String logFileName;

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
        return "basket_repo/" + loadFileName + "." + loadFileFormat;
    }

    public void setLoadFileName(String loadFileName) {
        this.loadFileName = loadFileName;
    }

    public String getSaveFileName() {
        return "basket_repo/" + saveFileName + "." + saveFileFormat;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public String getLoadFileFormat() {
        return loadFileFormat;
    }

    public void setLoadFileFormat(String loadFileFormat) {
        this.loadFileFormat = loadFileFormat;
    }

    public String getSaveFileFormat() {
        return saveFileFormat;
    }

    public String getLogFileName() {
        return "basket_repo/" + logFileName + ".csv";
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void setSaveFileFormat(String saveFileFormat) {
        this.saveFileFormat = saveFileFormat;
    }

    @Override
    public String toString() {
        return "<config>\n"
                +"<load>\n"
                + "<enabled>" + loadEnabled + "</enabled>\n"
                + "<fileName>" + loadFileName + "</fileName>\n"
                + "<format>" + loadFileFormat + "</format>\n"
                + "</load>\n"
                + "<save>\n"
                + "<enabled>" + saveEnabled + "</enabled>\n"
                + "<fileName>" + saveFileName + "</fileName>\n"
                + "<format>" + saveFileFormat + "</format>\n"
                + "</save>\n"
                + "<log>\n"
                + "<enabled>" + logEnabled + "</enabled>\n"
                + "<fileName>" + logFileName + "</fileName>\n"
                + "</log>\n"
                +"</config>";
    }
}
