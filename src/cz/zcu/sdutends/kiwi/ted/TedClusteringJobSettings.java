package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.sdutends.kiwi.IrJobSettings;

public class TedClusteringJobSettings extends IrJobSettings {

    private String inputDirectory = "storage/ted/talks";
    private String clusteringFile = "storage/ted/clustering/clustering-data.txt";
    private String outputDirectory = "storage/ted/clustering/output";

    public String getInputDirectory() {
        return inputDirectory;
    }

    public TedClusteringJobSettings setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
        return this;
    }

    public String getClusteringFile() {
        return clusteringFile;
    }

    public TedClusteringJobSettings setClusteringFile(String clusteringFile) {
        this.clusteringFile = clusteringFile;
        return this;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public TedClusteringJobSettings setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }
}
