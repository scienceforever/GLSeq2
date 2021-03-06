source("GLSeq.Util.R")

##################
# Load and check variables
##################
### Create QC Folder

create.QC.folder <- function(dest.dir,text.add){
  if (is.null(dest.dir) || is.null(text.add)) stop("Arguments should not be NULL")
  dest.dir <- trailDirCheck(dest.dir)
  quality.check.folder <- paste(dest.dir,text.add,".DataPrep",sep="")
  create.folder <- paste("mkdir",quality.check.folder)
  printOrExecute(create.folder,Condor)
  quality.check.folder
}


### Makes sure the presplit setting is correctly set.
check.presplit <- function(paired.end,presplit){
  if (is.null(paired.end) || is.null(presplit))  stop("Arguments should not be NULL")
  if (!is.logical(paired.end) || !is.logical(presplit)) stop("Arguments must be LOGICALS")
  # A file can only be presplit if it is paired ended, so let's cover any misoptioning in that regard here.
  if (!paired.end) presplit <- FALSE
  presplit
}

### Makes sure that the number of streams is correctly set.
check.nStreamsDataPrep <- function(fqFiles,nStreamsDataPrep){
  if (is.null(fqFiles) || is.null(nStreamsDataPrep))  stop("Arguments should not be NULL")
  # Problems can occur if the user tries to initiate more streams than tehre are files.
  # Let's correct that here by just setting the max as the length of files if that is the case.
  if (nStreamsDataPrep > length(fqFiles)) nStreamsDataPrep <- length(fqFiles)
  nStreamsDataPrep
}

### Checks if the program has found files to process.
check.ifFiles <- function(fqFiles.zip,fqFiles.unzip){
  # Stop the process if there are no files found.
  if (is.null(fqFiles.zip) && is.null(fqFiles.unzip)) stop("Please check the list of raw FASTQ files and the contents of the raw directory \n Please remember that files must be in .fq or .fq.gz format \n")
  NULL
}

##################
# Load and prepare files
##################

# Gets files that are unzipped.
# These files have .fq or .fastq extensions
get.files.unzipped <- function(raw.dir,libList){
  if (is.null(libList)){
    print(raw.dir)
    raw.dir <- trailDirCheck(raw.dir)
    if (length(unique(raw.dir)) == 1){
      # Takes either fasta or fq files
      fqFiles.unzip <- dir(raw.dir[1])[grep(".fq$|.fastq$", dir(raw.dir[1]))]
    }
  } else{
      # Takes either fasta or fq files
      fqFiles.unzip <- libList[grep(".fq$|.fastq$", libList)]
    }
  fqFiles.unzip
}

# Copies all the unzipped files from the raw directory
# that match the .fq or .fastq extension into the dest.dir folder.
copy.files.to.dest.unzipped <- function(raw.dir,dest.dir,libList){
  if (is.null(dest.dir)) stop("Arguments should not be NULL")
  dest.dir <- trailDirCheck(dest.dir)

  copy <- ""

  if (is.null(libList)) {
    raw.dir <- trailDirCheck(raw.dir)
    # Copy all relevant file types into the folder.
    fqFiles <- paste(raw.dir,"*.fq",sep="")
    fastqFiles <- paste(raw.dir,"*.fastq",sep="")
    copy <- paste("cp",fqFiles,dest.dir,"; cp",fastqFiles,dest.dir)
  } else {
    for (i in 1:length(libList)) {
      # Make it so that the liblist will know if to add the raw.dir or not.
      if (substr(libList[i],1,1) == "/"){
        copy.fastq <- paste("cp",libList[i],dest.dir)
        copy <- paste(copy,copy.fastq,"&")
      } else{
        copy.fastq <- paste("cp",paste(raw.dir,libList[i],sep=""),dest.dir)
        copy <- paste(copy,copy.fastq,"&")
      }
    }
  }
  print(copy)
  printOrExecute(copy,Condor)
  copy
}

# Copies all the zipped files from the raw directory
# that match the .gz extension into the dest.dir folder
copy.files.to.dest.zipped <- function(raw.dir,dest.dir,libList){
  if (is.null(dest.dir)) stop("Arguments should not be NULL")

  copy <- ""
  if (is.null(libList)){
    raw.dir <- trailDirCheck(raw.dir)
    dest.dir <- trailDirCheck(dest.dir)
    # Copy all relevant file types into the folder
    gzFiles <- paste(raw.dir,"*.gz",sep="")
    copy <- paste("cp",gzFiles,dest.dir)
  } else {
    for (i in 1:length(libList)){
      copy.fastq <- paste("cp",libList[i],dest.dir)
      copy <- paste(copy,copy.fastq,"&")
    }
    copy <- paste(copy,"wait")
  }
  printOrExecute(copy,Condor)
  copy
}

# Gets a list of all the zipped files
get.files.zipped <- function(raw.dir,libList){
  if (is.null(libList)){
    raw.dir <- trailDirCheck(raw.dir)
    # Gets the zipped files with the compressed extension .gz
    if (length(unique(raw.dir)) == 1) {
      fqFiles.zip <- dir(raw.dir[1])[grep(".fq.gz$|.fastq.gz$", dir(raw.dir[1]))]
    }
  } else {
      fqFiles.zip <- libList[grep(".fq.gz$|.fastq.gz$", libList)]
    }
  fqFiles.zip
}

# For the unzipped files, we don't remove anything from them.
# This function is here, though, in case we decide to change how our files
# Are structured.
prepare.unzipped.file.names <- function(fqFiles.unzip){
  if (is.null(fqFiles.unzip))  stop("Arguments should not be NULL")
  # Assumes the file is in the format of {name}_#.fq (Presplit) OR {name}_fq
  # Thus, the file example.1.fq along with its pair of example.2.fq
  # would become example.1.fq and example.2.fq
  # and an unsplit file example.fq would become example.fq
  fqFiles <- substr(fqFiles.unzip, 1, nchar(fqFiles.unzip)-0)
  fqFiles
}

# Removes the .gz extension from zipped files.
prepare.zipped.file.names <- function(fqFiles.zip){
  if (is.null(fqFiles.zip))  stop("Arguments should not be NULL")
  for (i in 1:length(fqFiles.zip)) {
    # Remove any prior absolute file path.
    fqFiles.zip[i] <- sub(".*/",'', fqFiles.zip[i])
  }
  # Assumes file is in the format of {name}_#.fq.gz (Presplit) OR {name}.fq.gz (Unsplit)
  # Thus, the file example.1.fq.gz along with its pair of example.2.fq.gz
  # would become example.1.fq and example.2.fq
  # and an unsplit file example.fq.gz would become example.fq
  fqFiles <- substr(fqFiles.zip, 1, nchar(fqFiles.zip)-3)
  fqFiles
}

# Divides up the data in chunks based on the set number of data streams.
# This is for presplit files (Paired files already found in .1.fq, .2.fq format)
chunk.data.files.presplit <- function(fqFiles,nStreamsDataPrep){
  if (is.null(fqFiles) || is.null(nStreamsDataPrep))  stop("Arguments should not be NULL")
  nStreamsDataPrep <- check.nStreamsDataPrep(fqFiles,nStreamsDataPrep)
  chunk <- function(x, n) split(x, sort(rank(x) %% n)) # commonly known solution to divide data equally
  rangelist.Dataprep <- chunk(2*(1:(length(fqFiles)/2)), nStreamsDataPrep)
  rangelist.Dataprep
}

# Divides up the data in chunks based on the set number of data streams.
# This is for unsplit or SE files.
chunk.data.files.unsplit <- function(fqFiles,nStreamsDataPrep){
  if (is.null(fqFiles) || is.null(nStreamsDataPrep))  stop("Arguments should not be NULL")
  nStreamsDataPrep <- check.nStreamsDataPrep(fqFiles,nStreamsDataPrep)
  chunk <- function(x, n) split(x, sort(rank(x) %% n)) # commonly known solution to divide data equally
  rangelist.Dataprep <- chunk(1:length(fqFiles), nStreamsDataPrep)
  rangelist.Dataprep
}

# Grabs the artificial fq sequence that the trimmomatic uses.
copy.artificial.fq <- function(artificial.fq,dest.dir,Condor=FALSE){
  if (is.null(artificial.fq) || is.null(dest.dir)) stop("Arguments should not be NULL")
  dest.dir <- trailDirCheck(dest.dir)
  copy <- paste("cp",artificial.fq,dest.dir)
  printOrExecute(copy,Condor)
  copy
}

store.artificial.seqs.file <- function(artificial.fq,qcFolder){
  if(is.null(artificial.fq) || is.null(qcFolder)) stop("Arguments should not be NULL")
  qcFolder <- trailDirCheck(qcFolder)
  move.to.storage <- paste("mv",artificial.fq,qcFolder)
  move.to.storage
}

# Strips the string of any extensions we add or exist on the file.
# This includes numbering (.#), zipped extension (gz), or file type extension (fastq vs fq)
get.file.base <- function(fqFile){
  if (grepl(".gz$",fqFile)){
    fqFile <- substr(fqFile, 1, nchar(fqFile)-3)
  }

  if (grepl(".fastq$",fqFile)){
    fqFile <- substr(fqFile, 1, nchar(fqFile)-6)
  }  else if (grepl(".fq$",fqFile)){
    fqFile <- substr(fqFile, 1, nchar(fqFile)-3)
  }

  if (grepl(".1$",fqFile)){
    fqFile <- substr(fqFile, 1, nchar(fqFile)-2)
  } else if (grepl(".2$",fqFile)){
    fqFile <- substr(fqFile, 1, nchar(fqFile)-2)
  }
  fqFile
}

############ Shell commands used by all protocols.
# Copies a file in from the raw directory to the destination directory
copy.file<- function(raw.dir,fqFile,dest.dir){
  if (is.null(raw.dir) || is.null(fqFile) || is.null(dest.dir)) stop("Arguments should not be NULL")
  dest.dir <- trailDirCheck(dest.dir)
  raw.dir <- trailDirCheck(raw.dir)
  #
  copy.comm <- paste("cp",paste(raw.dir,fqFile,sep=""),dest.dir)
  copy.comm
}

# Uses gunzip to unzip a gz zipped file.
unzip.gz.files <- function(fqFile.zip){
  if (is.null(fqFile.zip)) stop("Arguments should not be NULL")
  #
  gunzip.comm <- paste("gunzip",fqFile.zip,"&")
  gunzip.comm
}

###############
# Paired end processing
###############



############# NAMING
first.read.name <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  first.read.filename <- paste(fqFile,"1.fq", sep=".")
  first.read.filename
}

second.read.name <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  second.read.filename <- paste(fqFile,"2.fq", sep=".")
  second.read.filename
}

left.dirty.name <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  leftDirtyFname <-  paste(fqFile,"dirty.1.fq", sep=".")
  leftDirtyFname
}

right.dirty.name <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  rightDirtyFname <-  paste(fqFile,"dirty.2.fq", sep=".")
  rightDirtyFname
}


############# SHELL COMMAND CONSTRUCTION
# Creates the command that will call the Trimmomatic Jar file to clip sequences.
trimAssemble.PE <- function(fqFile, trimPath, qScore, headcrop=12, artificial.fq,trimMin) {
  if (is.null(fqFile) || is.null(trimPath) || is.null(qScore) || is.null(artificial.fq) || is.null(trimMin)){
    stop("Arguments should not be NULL")
  }
  if (substr(trimPath,nchar(trimPath),nchar(trimPath))=="/") stop("Invalid path to Trimmomatic JAR file")
  fqFile <- get.file.base(fqFile)
  first.read.file <- first.read.name(fqFile)
  second.read.file <- second.read.name(fqFile)

  dashqScore <- paste("-", qScore, sep="")
  #
  logName <- paste(first.read.file,"pairedtrim.log", sep=".")
  #
  pairedTrimmed.1 <- paste(first.read.file,"p.fq", sep=".")
  unpairedTrimmed.1 <- paste(first.read.file,"u.fq", sep=".")
  #
  pairedTrimmed.2 <- paste(second.read.file,"p.fq", sep=".")
  unpairedTrimmed.2 <- paste(second.read.file,"u.fq", sep=".")
  #
  trimParam <- paste("ILLUMINACLIP:", artificial.fq, ":2:30:10", " HEADCROP:", headcrop, " SLIDINGWINDOW:3:30 MINLEN:", trimMin, sep="")
  tcomm <- paste("java -jar", trimPath, "PE -threads 20", dashqScore, "-trimlog", logName, first.read.file, second.read.file, pairedTrimmed.1, unpairedTrimmed.1, pairedTrimmed.2, unpairedTrimmed.2, trimParam)
  # Return
  tcomm
}

# Moves some file names around to make it so that the resulting file will have the same name as the base file.
file.shuffle.PE <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  fqFile <- get.file.base(fqFile)
  first.read.file <- first.read.name(fqFile)
  second.read.file <- second.read.name(fqFile)
  leftDirtyFname <- left.dirty.name(fqFile)
  rightDirtyFname <- right.dirty.name(fqFile)
  pairedTrimmed.1 <-  paste(fqFile,".1.p.fq", sep="")
  pairedTrimmed.2 <- paste(fqFile,".2.p.fq", sep="")
  #
  fileShuffle <- paste("mv", first.read.file, leftDirtyFname, "&&", "mv", second.read.file, rightDirtyFname, "&&", "mv", pairedTrimmed.1, first.read.file, "&&", "mv", pairedTrimmed.2, second.read.file)
  #
  #
  fileShuffle
}

# Calls a bit of ruby code that splits an unsplit file into two files, file.1.fq and file.2.fq
split.unsplit.files.PE <- function(base.dir,fqFile){
  if (is.null(dest.dir) || is.null(fqFile)) stop("Arguments should not be NULL")
  dest.dir <- trailDirCheck(dest.dir)
  fqFile.base <- get.file.base(fqFile)
  first.read.filename <- first.read.name(fqFile.base)
  second.read.filename <- second.read.name(fqFile.base)
  # Shell script incorporates ruby code to split files.  Ruby code was found at Seqanswers.com
  split.comm <- paste("bash",paste(base.dir,"DataPrepFileSplit.sh",sep=""),fqFile,first.read.filename,second.read.filename)
  split.comm
}

# Calls the Fastqc quality check on the initial files.
preQualityCheck.PE <- function(fastqcPath, fqFile,qcFolder,first){
  if (is.null(fastqcPath) || is.null(fqFile) || is.null(qcFolder)) stop("Arguments should not be NULL")
  qcFolder <- trailDirCheck(qcFolder)
  fqFile <- get.file.base(fqFile)
  leftDirtyFname <- left.dirty.name(fqFile)
  rightDirtyFname <- right.dirty.name(fqFile)
  # The first param was added as parallelizing the QC too much can cause crashes.
  if (first){
  preQC <- paste(fastqcPath,"-o",qcFolder,leftDirtyFname, rightDirtyFname)
  } else{
    preQC <- paste(leftDirtyFname, rightDirtyFname)
  }
  preQC
}
# Calls the Fastqc quality check on the produced files.
postQualityCheck.PE <- function(fastqcPath,fqFile,qcFolder,first){
  if (is.null(fastqcPath) || is.null(fqFile) || is.null(qcFolder)) stop("Arguments should not be NULL")
  qcFolder <- trailDirCheck(qcFolder)
  fqFile <- get.file.base(fqFile)
  first.read.filename <- first.read.name(fqFile)
  second.read.filename <- second.read.name(fqFile)
  # The first param was added as parallelizing the QC too much can cause crashes.
  if (first){
    postQC <- paste(fastqcPath,"-o",qcFolder, first.read.filename, second.read.filename)
  } else{
    postQC <- paste(first.read.filename, second.read.filename)
  }
  postQC
}

# Removes a bunch of files that were used during the processing, but no longer are needed
remove.unneeded.files <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  fqFile <- get.file.base(fqFile)
  #
  first.read.file <- first.read.name(fqFile)
  second.read.file <- second.read.name(fqFile)
  leftDirtyFname <- left.dirty.name(fqFile)
  rightDirtyFname <- right.dirty.name(fqFile)
  unpairedTrimmed.1 <- paste(first.read.file,"u.fq",sep=".")
  unpairedTrimmed.2 <- paste(second.read.file,"u.fq",sep=".")
  #
  remove.command <- paste("rm",leftDirtyFname,"; rm",rightDirtyFname,"; rm",unpairedTrimmed.1,"; rm",unpairedTrimmed.2)
  remove.command
}

move.paired.files.PE <- function(fqFile,qcFolder){
  if (is.null(fqFile) || is.null(qcFolder)) stop("Arguments should not be NULL")
  qcFolder <- trailDirCheck(qcFolder)
  fqFile <- get.file.base(fqFile)
  first.read.file <- first.read.name(fqFile)
  second.read.file <- second.read.name(fqFile)
  logName.first <- paste(first.read.file, "pairedtrim.log", sep=".")
  #logName.second <- paste(second.read.file, "pairedtrim.log", sep=".")
  command <- paste("mv",logName.first,qcFolder)
  command
}

###############
# Single end processing
###############

############# NAMING
dirty.name.SE <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  dirty.name <-  paste(fqFile,"dirty.fq",sep=".")
  dirty.name
}

final.name.SE <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  final.name <-  paste(fqFile, ".fq", sep="")
  final.name
}

############# SHELL COMMAND CONSTRUCTION
# Creates the command that will call the Trimmomatic Jar file to clip sequences.
trimAssemble.SE <- function(fqFile, trimPath, qScore, headcrop=12, artificial.fq, trimMin) {
  if (is.null(fqFile) || is.null(trimPath) || is.null(qScore) || is.null(artificial.fq) || is.null(trimMin)){
    stop("Arguments should not be NULL")
  }
  if (substr(trimPath,nchar(trimPath),nchar(trimPath))=="/") stop("Invalid path to Trimmomatic JAR file")
  fqFile.base <- get.file.base(fqFile)
  dashqScore <- paste("-", qScore, sep="")
  logName <- paste(fqFile, "pairedtrim.log", sep=".")
  pairedTrimmed.1 <- paste(fqFile.base, "p.fq", sep=".") # '.fq' is added here for single end libraries (it is added at the splitting stage for the paired-end)
  trimParam <- paste("ILLUMINACLIP:", artificial.fq, ":2:30:10", " HEADCROP:", headcrop, " SLIDINGWINDOW:3:30 MINLEN:", trimMin, sep="")
  tcomm <- paste("java -jar", trimPath, "SE -threads 20", dashqScore, "-trimlog", logName, fqFile,  pairedTrimmed.1, trimParam)
  tcomm
}
# Renames it so that the final resulting file has the same name as the original.
file.shuffle.SE <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  initial.name <- fqFile
  fqFile <- get.file.base(fqFile)
  final.name <- final.name.SE(fqFile)
  dirty.name <- dirty.name.SE(fqFile)
  trimmed.name <- paste(fqFile,".p.fq",sep="")
  #
  # Move the original file and call it the dirty file
  # Also move the trimmed file and then call it the original final name so that it is the cleanest
  fileShuffle <- paste("mv", initial.name , dirty.name, " && ",  "mv", trimmed.name, final.name)
  #
  fileShuffle
}

# Quality checks the initial file
preQualityCheck.SE <- function(fastqcPath, fqFile,qcFolder,first){
  if (is.null(fastqcPath) || is.null(fqFile) || is.null(qcFolder)) stop("Arguments should not be NULL")
  qcFolder <- trailDirCheck(qcFolder)
  fqFile <- get.file.base(fqFile)
  dirty.name <- dirty.name.SE(fqFile)
  if (first){
    preQC <- paste(fastqcPath,"-o",qcFolder,dirty.name)
  } else{
    preQC <- dirty.name
  }
  preQC
}
# Quality checks the resulting file.
postQualityCheck.SE <- function(fastqcPath,fqFile,qcFolder,first){
  if (is.null(fastqcPath) || is.null(fqFile) || is.null(qcFolder)) stop("Arguments should not be NULL")
  qcFolder <- trailDirCheck(qcFolder)
  fqFile <- get.file.base(fqFile)
  final.name <- final.name.SE(fqFile)
  if (first){
  postQC <- paste(fastqcPath,"-o",qcFolder,final.name)
  } else{
    postQC <- final.name
  }
  postQC
}
# Removes all the unneeded files.
remove.unneeded.files.SE <- function(fqFile){
  if (is.null(fqFile)) stop("Arguments should not be NULL")
  fqFile <- get.file.base(fqFile)
  dirty.name <- dirty.name.SE(fqFile)
  remove.command <- paste("rm",dirty.name)
}

move.paired.files.SE <- function(fqFile,qcFolder){
  if (is.null(fqFile) || is.null(qcFolder)) stop("Arguments should not be NULL")
  qcFolder <- trailDirCheck(qcFolder)
  logName <- paste(fqFile, "pairedtrim.log", sep=".")
  command <- paste("mv",logName,qcFolder)
  command
}

convert.to.absolute.paths <- function(dest.dir,fqFiles){
  if (is.null(fqFiles) || is.null(dest.dir)) stop("Arguments should not be NULL")
  dest.dir <- trailDirCheck(dest.dir)
  for (i in 1:length(fqFiles)){
      fqFiles[i] <- paste(dest.dir,fqFiles[i],sep="")
  }
  fqFiles
}