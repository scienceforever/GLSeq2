package org.glbrc.glseq2.Project_Files;

///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  UI.java
// File:             Attributes.java  
//
//
// Author:           Michael Lampe | MrLampe@Wisc.edu
///////////////////////////////////////////////////////////////////////////////
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Attributes {

  // Data and Library Attributes
  public String directory = "";
  public String unzipped = "FALSE";
  public String directoryFq = "";
  public String rawFileNames = "";
  public String strain = "";
  public String pairedEnd = "TRUE";
  public String seqPlatform = "";
  public String qualityScores = "";
  public String libstrands = "";
  public String libNchar = "0";
  public String libList = "NULL";
  public String countableSamDir = "";
  public String presplit = "FALSE";
  public String prevRunDirectory = "";
  public String prevRunName = "";
  // Reference Attributes
  public String referenceGenome = "";
  public String referenceFasta = "";
  public String referenceGff = "";
  public String gtfFeatureColumn = "0";
  public String idAttr = "";

  // Run Attributes
  public String destinationDirectory = "";
  public String featureCounts = "";
  public String rsem = "";
  public String htseq = "";
  public String cufflinks = "";
  public String alignmentAlgor = "";
  public String numberCores = "0";
  public String numberStreams = "0";
  public String numberStreamsDataPrep = "0";
  public String gpuAcceleration = "FALSE";

  // Pre-Processing Attributes
  public String readTrim = "FALSE";
  public String trimHead = "0";
  public String minTrim = "0";
  public String artificialFasta = "";

  // Common Processing Attributes
  public String strandExtract = "FALSE";

  // RSEM Attributes
  public String compConf = "FALSE";
  public String fragMaxLength = "0";
  public String ciMem = "0";
  public String genobam = "FALSE";

  // Environment Attributes
  public String scriptDirectory = "";
  public String trimPath = "";
  public String picardToolsPath = "";
  public String fastqcPath = "";
  public String bwaPath = "";
  public String bam2wigPath = "";
  public String cushawPath = "";
  public String cushawGpuPath = "";
  public String cushawIndexPath = "";
  public String topHatPath = "";
  public String destDirTest = "";

  // RunId
  public String runId = "";

  public Attributes() {

  }

  public Attributes(Attributes anotherAttributes) {
    Field[] fields = anotherAttributes.getClass().getDeclaredFields();
    for (Field field : fields) {
      try {
        Field currentField = this.getClass().getField(field.getName());
        currentField.set(this, field.get(anotherAttributes));
      } catch (IllegalArgumentException | IllegalAccessException e) {

      } catch (NoSuchFieldException e) {
      } catch (SecurityException e) {

      }
    }
  }

  /**
   * Creates a new or replaces the current attribute file and adds all the field
   * data to that attribute file.
   * 
   * @throws IllegalAccessException .
   * @throws IllegalArgumentException .
   * @throws SecurityException .
   * @throws NoSuchFieldException
   *
   */
  public void setAttributes() throws SecurityException {
    Field field = null;
    Scanner scnr = null;
    File config = null;
    try {
      config = new File("AttributesConfig.txt");
      scnr = new Scanner(config);
    } catch (Exception e) {
      // This is expected
      // If there is no AttributeFile because of no previous runs, etc.
    }
    // Looks through the whole document for variables that match field names
    while (scnr.hasNextLine()) {
      String temp = scnr.nextLine();
      if (temp.contains("= ")) {
        String[] tempArray = temp.split(" = ");
        try {
          field = this.getClass().getDeclaredField(tempArray[0]);
        } catch (NoSuchFieldException e) {
          // This is expected
          // Not all fields in the calling file may be actual fields.
        }
        try {
          field.set(this, (tempArray[1]));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
          // This is expected
        }
      }
    }
    scnr.close();
  }

  /**
   * Creates a new or replaces the current attribute file and adds all the field
   * data to that attribute file.
   * 
   * <p>
   * This method uses an input attributeFile.R to take old data and load it in.
   *
   */
  public void setAttributes(File attributeFile) {
    Field field;
    Scanner scnr = null;
    try {
      scnr = new Scanner(attributeFile);
      // Looks through the whole document for variables that match field
      // names
      while (scnr.hasNextLine()) {
        String temp = scnr.nextLine();
        if (temp.contains("=")) {
          String[] tempArray = temp.split(" = ");
          try {
            field = this.getClass().getDeclaredField(tempArray[0]);
            field.set(this, tempArray[1]);
          } catch (Exception e) {
            // This is expected if no field exists, etc.
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Attribute file not found");
    } finally {
      // Makes sure to close the scanner
      scnr.close();
    }
  }

  /**
   * Future use case for possible SSH construction of AttributeFiles in a
   * programmatic manner. Otherwise also let's the user not use the UI when
   * accessing it from a server.
   * 
   * @param inputArgs
   *          An array of strings passed in from another source
   */
  public void setAttributes(String[] inputArgs) {
    Field field;
    for (String param : inputArgs) {
      String[] parts = param.split("=");
      try {
        field = this.getClass().getDeclaredField(parts[0]);
        field.set(this, parts[1]);
      } catch (Exception e) {
        System.out.println("Invalid field param");
      }
    }
  }

  // Setters for all the variables.
  public void setDirectory(String directory) {
    this.directory = directory;
  }

  public void setUnzipped(String unzipped) {
    this.unzipped = unzipped;
  }

  public void setDirectoryFq(String directoryFq) {
    this.directoryFq = directoryFq;
  }

  public void setRawFileNames(String rawFileNames) {
    this.rawFileNames = rawFileNames;
  }

  public void setStrain(String strain) {
    this.strain = strain;
  }

  public void setPairedEnd(String pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

  public void setSeqPlatform(String seqPlatform) {
    this.seqPlatform = seqPlatform;
  }

  public void setQScores(String qualityScores) {
    this.qualityScores = qualityScores;
  }

  public void setLibstrand(String libstrands) {
    this.libstrands = libstrands;
  }

  public void setLibNchar(String libNchar) {
    this.libNchar = libNchar;
  }

  public void setLibList(String libList) {
    this.libList = libList;
  }

  public void setCountableSamDir(String countableSamDir) {
    this.countableSamDir = countableSamDir;
  }

  public void setPresplit(String presplit) {
    this.presplit = presplit;
  }

  // Reference Attribute Setters
  public void setRGenome(String rerefenceGenome) {
    this.referenceGenome = rerefenceGenome;
  }

  public void setFasta(String referenceFasta) {
    this.referenceFasta = referenceFasta;
  }

  public void setGffname(String referenceGff) {
    this.referenceGff = referenceGff;
  }

  public void setFeatureColumn(String gtfFeatureColumn) {
    this.gtfFeatureColumn = gtfFeatureColumn;
  }

  public void setIdAttr(String idAttr) {
    this.idAttr = idAttr;
  }

  // Run Attributes
  public void setScriptDirectory(String scriptDirectory) {
    this.scriptDirectory = scriptDirectory;
  }

  public void setDestinationDirectory(String destinationDirectory) {
    this.destinationDirectory = destinationDirectory;
  }

  public void setCores(String numberCores) {
    this.numberCores = numberCores;
  }

  public void setStreams(String numberStreams) {
    this.numberStreams = numberStreams;
  }

  public void setStreamsDataPrep(String numberStreamsDataPrep) {
    this.numberStreamsDataPrep = numberStreamsDataPrep;
  }

  public void setGpuAccel(String gpuAcceleration) {
    this.gpuAcceleration = gpuAcceleration;
  }

  // Pre-Processing Attributes
  public void setReadTrim(String readTrim) {
    this.readTrim = readTrim;
  }

  public void setTrimhead(String trimHead) {
    this.trimHead = trimHead;
  }

  public void setMinTrim(String minTrim) {
    this.minTrim = minTrim;
  }

  public void setArtificialFasta(String artificialFasta) {
    this.artificialFasta = artificialFasta;
  }

  // Common Processing Attributes
  public void setStrandExtract(String strandExtract) {
    this.strandExtract = strandExtract;
  }

  // RSEM Attributes
  public void setCompConf(String compConf) {
    this.compConf = compConf;
  }

  public void setFragMaxLength(String fragMaxLength) {
    this.fragMaxLength = fragMaxLength;
  }

  public void setCiMem(String ciMem) {
    this.ciMem = ciMem;
  }

  public void setGenoBam(String genobam) {
    this.genobam = genobam;
  }

  // Environment Attributes
  public void setTrimPath(String trimPath) {
    this.trimPath = trimPath;
  }

  public void setPicardToolsPath(String picardToolsPath) {
    this.picardToolsPath = picardToolsPath;
  }

  public void setFastqcPath(String fastqcPath) {
    this.fastqcPath = fastqcPath;
  }

  public void setBwaPath(String bwaPath) {
    this.bwaPath = bwaPath;
  }

  public void setBam2WigPath(String bam2wigPath) {
    this.bam2wigPath = bam2wigPath;
  }

  public void setCushawPath(String cushawPath) {
    this.cushawPath = cushawPath;
  }

  public void setCushawGpuPath(String cushawGpuPath) {
    this.cushawGpuPath = cushawGpuPath;
  }

  public void setTopHatPath(String topHatPath) {
    this.topHatPath = topHatPath;
  }

  public void setDestDirTest(String destDirTest) {
    this.destDirTest = destDirTest;
  }

  //
  //
  public void setCushawIndexPath(String cushawIndexPath) {
    this.cushawIndexPath = cushawIndexPath;
  }

  public void setHtseq(String htseq) {
    this.htseq = htseq;
  }

  public void setCufflinks(String cufflinks) {
    this.cufflinks = cufflinks;
  }

  public void setFeatureCounts(String featureCounts) {
    this.featureCounts = featureCounts;
  }

  public void setRsem(String rsem) {
    this.rsem = rsem;
  }

  public void setaAlgor(String alignmentAlgor) {
    this.alignmentAlgor = alignmentAlgor;
  }

  public String getPrevRunDirectory() {
    return prevRunDirectory;
  }

  public void setPrevRunDirectory(String prevRunDirectory) {
    this.prevRunDirectory = prevRunDirectory;
  }

  //
  public String getCushawIndexPath() {
    return cushawIndexPath;
  }

  public String getPrevRunName() {
    return prevRunName;
  }

  public void setPrevRunName(String prevRunName) {
    this.prevRunName = prevRunName;
  }

  public String getHtseq() {
    return htseq;
  }

  public String getCufflinks() {
    return cufflinks;
  }

  public String getFeatureCounts() {
    return featureCounts;
  }

  public String getRsem() {
    return rsem;
  }

  public String getaAlgor() {
    return alignmentAlgor;
  }

  // Data and Library Getters
  public String getDirectory() {
    return directory;
  }

  public String getUnzipped() {
    return unzipped;
  }

  public String getDirectoryFq() {
    return directoryFq;
  }

  public String getRawFileNames() {
    return rawFileNames;
  }

  public String getStrain() {
    return strain;
  }

  public String getPairedEnd() {
    return pairedEnd;
  }

  public String getSeqPlatform() {
    return seqPlatform;
  }

  public String getQScores() {
    return qualityScores;
  }

  public String getLibstrand() {
    return libstrands;
  }

  public String getLibNchar() {
    return libNchar;
  }

  public String getLibList() {
    return libList;
  }

  public String getCountableSamDir() {
    return countableSamDir;
  }

  public String getPresplit() {
    return presplit;
  }

  // Reference Attribute Getters
  public String getRGenome() {
    return referenceGenome;
  }

  public String getFasta() {
    return referenceFasta;
  }

  public String getGff() {
    return referenceGff;
  }

  public String getFeatureColumn() {
    return gtfFeatureColumn;
  }

  public String getIdAttr() {
    return idAttr;
  }

  // Run Attributes
  public String getScriptDirectory() {
    return scriptDirectory;
  }

  public String getDestinationDirectory() {
    return destinationDirectory;
  }

  public String getCores() {
    return numberCores;
  }

  public String getStreams() {
    return numberStreams;
  }

  public String getStreamsDataPrep() {
    return numberStreamsDataPrep;
  }

  public String getGpuAccel() {
    return gpuAcceleration;
  }

  // Pre-Processing Attributes
  public String getReadTrim() {
    return readTrim;
  }

  public String getTrimhead() {
    return trimHead;
  }

  public String getMinTrim() {
    return minTrim;
  }

  public String getArtificialFasta() {
    return artificialFasta;
  }

  // Common Processing Attributes
  public String getStrandExtract() {
    return strandExtract;
  }

  // RSEM Attributes
  public String getCompConf() {
    return compConf;
  }

  public String getFragMaxLength() {
    return fragMaxLength;
  }

  public String getCiMem() {
    return ciMem;
  }

  public String getGenoBam() {
    return genobam;
  }

  // Environment Attributes
  public String getTrimPath() {
    return trimPath;
  }

  public String getPicardToolsPath() {
    return picardToolsPath;
  }

  public String getFastqcPath() {
    return fastqcPath;
  }

  public String getBwaPath() {
    return bwaPath;
  }

  public String getBam2WigPath() {
    return bam2wigPath;
  }

  public String getCushawPath() {
    return cushawPath;
  }

  public String getCushawGpuPath() {
    return cushawGpuPath;
  }

  public String getTopHatPath() {
    return topHatPath;
  }

  public String getDestDirTest() {
    return destDirTest;
  }

  /**
   * 
   * @return AttributeFileLocation - The new attribute file's path
   * @throws IOException .
   */
  public String writeAttributesFile(String file_name, String file_location) throws IOException {
    // Writes a new attribute file based on the current format that we have
    // outlined.
    // Saves it as an R file, with the current date.
    boolean changed = false;
    if (alignmentAlgor.equals("Cushaw-GPU")) {
      alignmentAlgor = "Cushaw";
      gpuAcceleration = "TRUE";
      changed = true;
    }

    // The file to be created
    File attributeFile = null;
    // Adds the date to the file
    Date current = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");

    // If null, names file for user
    if (file_name == null) {
      if (file_location == null) {
        attributeFile = new File("Attribute_" + ft.format(current) + "_"
            + String.valueOf(uniqueAttributeFile()) + ".R");
      } else {
        file_location = checkSlash(file_location);
        attributeFile = new File(file_location + "Attribute_" + ft.format(current) + "_"
            + String.valueOf(uniqueAttributeFile()) + ".R");
      }
    } else {
      // Custom file name set by the user.
      if (file_location == null) {
        attributeFile = new File(file_name + ".R");
      } else {
        file_location = checkSlash(file_location);
        attributeFile = new File(file_location + file_name + ".R");
      }
    }
    attributeFile.createNewFile();
    final String attFileLocation = attributeFile.getAbsolutePath();
    FileWriter writer = new FileWriter(attributeFile);
    // The entire attribute file in this format
    writer.write("#########################################################\n");
    writer.write("# Great Lakes Seq package for low-level processing of RNA-Seq data\n");
    writer.write("#########################################################\n");
    writer.write("#\n");
    writer.write("# This is a user generated attribute file created on " + ft.format(current)
        + "\n");
    writer.write("#\n");
    writer.write("##########################################################\n");
    writer.write("#\n");
    writer.write("# Usage: called from the GLSeq.top.R script;\n");
    writer.write("# multiple versions of local attribute files "
        + "may exist; the name of the particular version\n");
    writer.write("# may be supplied as an option to the GLSeq.top.R script\n");
    writer.write("#\n");
    writer.write("##########################################################\n");
    writer.write("#\n");
    writer.write("################################\n");
    writer.write("# DATA / LIBRARY OPTIONS\n");
    writer.write("###############################\n");
    writer.write("#\n");
    writer.write("# directory containing raw files\n");
    writer.write("# (may be non-writable!)\n");
    writer.write("raw.dir <-\"" + directory + "\"\n");
    writer.write("#\n");
    writer.write("# Files in the raw dir are normally compressed but may be not:\n");
    writer.write("unzipped <- " + unzipped.toUpperCase() + "\n");
    writer.write("#\n");
    writer
        .write("# directory contining ready-to-go (split+QC-processed) fq files (Oct 17, 2013)\n");
    writer.write("readyData.dir <- \"" + directoryFq + "\"\n");
    writer.write("#\n");
    writer.write("# raw file names: \n");
    writer.write("raw.fNames <- \"" + rawFileNames + "\"\n");
    writer.write("#\n");
    writer.write("# strain\n");
    writer.write("strain <- \"" + strain + "\"\n");
    writer.write("#\n");
    writer.write("# single / paired end\n");
    writer.write("paired.end <- " + pairedEnd.toUpperCase() + "\n");
    writer.write("#\n");
    writer.write("presplit <- " + presplit.toUpperCase() + "\n");
    writer.write("#\n");
    writer.write("# sequencing platform (used by CUSHAW, supported "
        + "values: capillary, ls454, illumina, solid, helicos, iontorrent, pacbio)\n");
    writer.write("seqPlatform <- \"" + seqPlatform + "\"\n");
    writer.write("#\n");
    writer.write("# quality scores format\n");
    writer.write("qScores <- \"" + qualityScores + "\"\n");
    writer.write("#\n");
    writer.write("# Strandness of the library (NULL, F, R) \n");
    writer.write("libstrand <- \"" + libstrands + "\"\n");
    writer.write("#\n");
    writer.write("# Number of unique characters in the beginning"
        + " of the each file (library ID length):\n");
    writer.write("libNchar <- " + libNchar + "\n");
    writer.write("#\n");
    writer.write("# Subset of the libraries to process "
        + "(optional; normally the list wil be generated from the actual directory content)\n");
    writer.write("libList <- " + libList + "\n");
    writer.write("#\n");
    writer.write("# Takes a directory of files with the end title"
        + " \"countable.sam\" and collects them for counting\n");
    writer.write("countable.sams.dir <- \"" + countableSamDir + "\"\n");
    writer.write("#\n");
    writer.write("# This is a previous run directory that was used.\n");
    writer.write("# It is the actual folder that was created when the run started\n");
    writer.write("previous.dir <- \"" + prevRunDirectory + "\"\n");
    writer.write("#\n");
    writer.write("# The name of the previous run\n");
    writer.write("previous.run.name <- \"" + prevRunName + "\"\n");
    writer.write("#\n");
    writer.write("###############################\n");
    writer.write("# REFERENCE OPTIONS\n");
    writer.write("###############################\n");
    writer.write("#\n");
    writer.write("# reference genome - the index directory"
        + " for the respective method (RSEM or BWA)\n");
    writer.write("# (must match the name of the  subfolder under base.dir):\n");
    writer.write("rGenome <- \"" + referenceGenome + "\"\n");
    writer.write("#\n");
    writer.write("# name of the reference fasta"
        + " file (may differ from the base name of the reference -\n");
    writer.write("# still, it should be located in the folder for the selected reference):\n");
    writer.write("refFASTAname <- \"" + referenceFasta + "\"\n");
    writer.write("#\n");
    writer.write("# name of the reference genomic features"
        + " file (may differ from the base name of the reference -\n");
    writer.write("# this will be eventually taken from"
        + " the database); it should be located in the folder for the selected feference)\n");
    writer.write("# gtf files may be used instead of"
        + " gff where applicable; the same object is used for both cases:\n");
    writer.write("refGFFname <- \"" + referenceGff + "\"\n");
    writer.write("# refGFFname <- \"Novosphingobium_3replicons.Clean.gff\"\n");
    writer.write("#\n");
    writer.write("# number of columns in the GTF file with"
        + " the gene / other IDs character string "
        + "(9, unless the file is non-standard for some reason):\n");
    writer.write("gtfFeatureColumn <- " + gtfFeatureColumn + "\n");
    writer.write("#\n");
    writer.write("# GFF attribute to be used as feature ID (HTSeq):\n");
    writer.write("idAttr <- \"" + idAttr + "\"\n");
    writer.write("# idAttr <- \"locus_tag\" # useful if \"gene_id\" has duplicated entries\n");
    writer.write("#\n");
    writer.write("###############################\n");
    writer.write("# RUN OPTIONS\n");
    writer.write("###############################\n");
    writer.write("#\n");
    writer.write("# the directory containing the GLSeq scripts:\n");
    writer.write("base.dir <- \"" + scriptDirectory + "\"\n");
    writer.write("#\n");
    writer.write("# Base of the destination directory (added May 9, 2013)\n");
    writer.write("# This should be located on a FAST volume (SCSI is recommended)\n");
    writer.write("# a particular subfolder names after "
        + "the run ID will be created by GLSeq below this folder\n");
    writer.write("dest.dir.base <- \"" + destinationDirectory + "\"\n");
    writer.write("#\n");
    writer.write("# number of cores to use\n");
    writer.write("nCores <- " + numberCores + "\n");
    writer.write("#\n");
    writer.write("# number of parallel computation streams for expression computation\n");
    writer.write("nStreams <-" + numberStreams + "\n");
    writer.write("#\n");
    writer.write("# number of parallel computation streams for data preparation\n");
    writer.write("# (may differ from the number of streams"
        + " for expression computation because of particular software demands) \n");
    writer.write("nStreamsDataPrep <- " + numberStreamsDataPrep + "\n");
    writer.write("#\n");
    writer.write("# the actual unique run ID - \n");
    writer.write("# text.add <- paste(expID, runAttempt, sep=\".\")\n");
    writer.write("# now is being generated inside GLSeq.top.R\n");
    writer.write("#\n");
    writer.write("# *** Alignment Algorithm ***\n");
    writer.write("aAlgor <- \"" + alignmentAlgor + "\" \n");
    writer.write("#\n");
    writer.write("#\n");
    // This is just assigning them as variables and just making them as a list
    // in the R file
    writer.write("# *** Counting Algorithm(s) ***\n");
    writer.write("HTSeq <- \"" + htseq + "\"\n");
    writer.write("FeatureCounts <- \"" + featureCounts + "\"\n");
    writer.write("RSEM <- \"" + rsem + "\"\n");
    writer.write("Cufflinks <- \"" + cufflinks + "\"\n");
    writer.write("cAlgor <- c(HTSeq,RSEM,FeatureCounts,Cufflinks)\n");
    writer.write("#\n");
    writer.write("#  GPU acceleration option for CUSHAW\n");
    writer.write("GPU.accel <- " + gpuAcceleration.toUpperCase() + "\n");
    writer.write("#\n");
    writer.write("###############################\n");
    writer.write("# PRE-PROCESSING OPTIONS\n");
    writer.write("###############################\n");
    writer.write("#\n");
    writer.write("# trim the reads and generate QC "
        + "reports for before- and after-trimming FASTQ files? \n");
    writer.write("readTrim <- " + readTrim.toUpperCase() + "\n");
    writer.write("#\n");
    writer.write("# minimum length of a trimmed read\n");
    writer.write("trimMin <- " + minTrim + "\n");
    writer.write("#\n");
    writer.write("# trimmomatic parameter values for HEADCROP  \n");
    writer.write("trimhead <- " + trimHead + "\n");
    writer.write("#\n");
    writer.write("# name of the FASTA file with artificial"
        + " sequences (adapters, primers etc) - must be located in the base.dir\n");
    writer.write("artificial.fq <- \"" + artificialFasta + "\"\n");
    writer.write("#\n");
    writer.write("###############################\n");
    writer.write("# COMMON PROCESSING OPTIONS\n");
    writer.write("###############################\n");
    writer.write("#\n");
    writer.write("# Extract explicit forward and reverse coverage from the original BAM file? \n");
    writer.write("strandExtract <- " + strandExtract.toUpperCase() + "\n");
    writer.write("#\n");
    writer.write("################################\n");
    writer.write("# RSEM OPTIONS\n");
    writer.write("################################\n");
    writer.write("#\n");
    writer.write("# compute confidence intervals? \n");
    writer.write("compConf <- " + compConf.toUpperCase() + "\n");
    writer.write("#\n");
    writer.write("# Maximal length of fragment (for paired-end libraries)\n");
    writer.write("fragMaxLength <- " + fragMaxLength + "\n");
    writer.write("#\n");
    writer.write("# Maximum size (MB) of the auxiliary buffer used "
        + "for computing credibility intervals (CI) - for RSEM (+extra 2Gb per stream)\n");
    writer.write("ciMem <- " + ciMem + "\n");
    writer.write("#\n");
    writer.write("# Output genome bam\n");
    writer.write("genobam <- " + genobam.toUpperCase() + "\n");
    writer.write("#\n");
    writer.write("################################\n");
    writer.write("# ENVIRONMENT\n");
    writer.write("################################\n");
    writer.write("#\n");
    writer.write("# path to Trimmomatic: \n");
    writer.write("trimPath <- '" + trimPath + "'\n");
    writer.write("#\n");
    writer.write("# path to PicardTools jar directory:\n");
    writer.write("# picardToolsPath <- '/soft/picard-tools-1.98/picard-tools-1.98/'\n");
    writer.write("picardToolsPath <- '" + picardToolsPath + "'\n");
    writer.write("#\n");
    writer.write("# path to fastqc:\n");
    writer.write("fastqcPath <- '" + fastqcPath + "'\n");
    writer.write("#\n");
    writer.write("# path to BWA\n");
    writer.write("bwaPath <- '" + bwaPath + "'\n");
    writer.write("#\n");
    writer.write("# path to the shell script that converts bam to wig\n");
    writer.write("bam2wigPath <- \"" + bam2wigPath + "\"\n");
    writer.write("#\n");
    writer.write("# path to CUSHAW\n");
    writer.write("CUSHAW.index.path <- \"" + cushawIndexPath + "\"\n");
    writer.write("CUSHAW.path <- \"" + cushawPath + "\"\n");
    writer.write("#\n");
    writer.write("# path to CUSHAW-GPU\n");
    writer.write("CUSHAW.GPU.path <- \"" + cushawGpuPath + "\"\n");
    writer.write("#\n");
    writer.write("# Path to the TopHat aligner\n");
    writer.write("TopHat.path <- \"" + topHatPath + "\"\n");
    writer.write("#\n");
    writer.write("# Logging system.  Will create general logs of"
        + " all the runs and put them in this other directory.\n");
    writer.write("destDirTest <- \"" + destDirTest + "\"\n");
    writer.write("#\n");
    writer.write("# End of Attribute File\n");
    writer.close();
    if (changed) {
      alignmentAlgor = "Cushaw-GPU";
    }
    return attFileLocation;
  }

  /**
   * 
   * @param fileName
   *          The name of the file the user wishes to create.
   * @throws IOException .
   */
  public void saveConfigFile(String fileName) throws IOException {
    // An array of all the fields
    final Field[] field = this.getClass().getDeclaredFields();
    File attributeFile;
    // Creates new or saves over the attribute config file.
    if (fileName == null) {
      attributeFile = new File("AttributesConfig.txt");
    } else {
      attributeFile = new File(fileName);
    }
    attributeFile.createNewFile();
    FileWriter writer = new FileWriter(attributeFile);
    // Short formatting warning
    writer.write("READ: Program requires that each config "
        + "option has one space, and only one space,\n");
    writer.write("after the equal sign.  If you are editing "
        + "the options from here, please remember this.\n");
    // Writes all the fields to the text file in the most minimal manner
    // possible.
    for (int i = 0; i < field.length; i++) {
      try {
        writer.write(field[i].getName() + " = " + field[i].get(this) + "\n");
      } catch (Exception e) {
        continue;
      }
    }
    writer.close();
  }

  /**
   * Makes a random number so that attribute files are unique.
   * 
   * @return A more unique number
   */
  private int uniqueAttributeFile() {
    Random rand = new Random();
    int value = rand.nextInt(10000);
    return value;
  }

  private String checkSlash(String file_name) {
    if (file_name.substring(file_name.length() - 1).equals("/")) {
      return file_name;
    } else {
      file_name = file_name + "/";
      return file_name;
    }
  }

  public void returnJson() {
    System.out.println("{\"attributes\":[");
    Field[] fields = this.getClass().getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      try {
        System.out.print(formatJson(fields[i].getName()));
        if (i < fields.length - 2){
          System.out.print(",");
        }
        System.out.println("");
      } catch (IllegalArgumentException e) {
        // Ignore
      } catch (SecurityException e) {
        // Ignore
      }
    }
    System.out.println("]}");
  }

  private String formatJson(String field_name) {
    String jsonKey = "{\"key\":";
    AttributesJSON current = Enum.valueOf(AttributesJSON.class, field_name);
    jsonKey += "\"" + current.name() + "\"";
    if (current.category != null){
      jsonKey += ", \"category\":\"" + current.category + "\"";
    }
    if (current.options != null){
      jsonKey += ", \"options\":[" + current.options + "]";
    }
    if (current.defaultVal != null){
      jsonKey += ", \"default\":\"" + current.defaultVal + "\"";
    }
    if (current.description != null){
      jsonKey += ", \"description\":\"" + current.description + "\"";
    }
    jsonKey += "}";
    return jsonKey;
  }
  
  public int getFieldNumber(){
    return this.getClass().getDeclaredFields().length;
  }
}