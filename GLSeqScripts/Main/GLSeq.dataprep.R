#########################################################
# Great Lakes Seq package for low-level processing of RNA-Seq data
# Oleg Moskvin; info@scienceforever.com
# April 2013
#########################################################
#
# Preparing the raw data
#
#########################################################
#
###########################
# ID of the computation run (text.add object) should be
# supplied to this script directly (by GLSeq.top.R) as
# the first and only argument
###########################
#
source("GLSeq.Util.R")
source(attrPath)
source("GLSeq.Dataprep.Functions.R")
#
###########################
# Files indicating readiness of the libraries (i.e. split FQ files):
###########################
files2watch.dataprep <- NULL
fqFiles.zip <- NULL
fqFiles.unzip <- NULL
###########################
# Create QC Folder
qcFolder <-  create.QC.folder(dest.dir,text.add)

if (unzipped){
  fqFiles.unzip <- get.files.unzipped(raw.dir,libList)
  fqFiles <- prepare.unzipped.file.names(fqFiles.unzip)
  copy.files.to.dest.unzipped(raw.dir,dest.dir,fqFiles.unzip)
}

if (!unzipped){
  fqFiles.zip <- get.files.zipped(raw.dir,libList)
  fqFiles <- prepare.zipped.file.names(fqFiles.zip)
  copy.files.to.dest.zipped(raw.dir,dest.dir,fqFiles.zip)
  unzip.comm <- ""
  for (i in 1:length(fqFiles.zip)) {
    # Just have the file name
    fqFiles.zip[i] <- sub(".*/",'', fqFiles.zip[i])

    if (is.null(libList)) {
      if (is.null(unzip.comm)) {
        unzip.comm <- unzip.gz.files(paste(dest.dir,fqFiles.zip[i],sep=""))
      } else {
        unzip.comm <- paste(unzip.comm,unzip.gz.files(paste(dest.dir,fqFiles.zip[i],sep="")))
      }
    } else {
      if (unzip.comm == "") {
        unzip.comm <- unzip.gz.files(paste(dest.dir,fqFiles.zip[i],sep=""))
      } else {
        unzip.comm <- paste(unzip.comm,unzip.gz.files(paste(dest.dir,fqFiles.zip[i],sep="")))
      }
    }
  }
  unzip.comm <- paste(unzip.comm,"wait")
  # Parallel Gunzip
  printOrExecute(unzip.comm,Condor)
}

# Most of the alignment stuffs adds the destination and such on their own, so let's not mess that up and return
# Just the relative file name
for (i in 1:length(fqFiles)) {
  fqFiles[i] <- sub(".*/",'', fqFiles[i])
}
relative.fqFiles <- fqFiles
fqFiles <- convert.to.absolute.paths(dest.dir,fqFiles)
#
check.ifFiles(fqFiles.zip,fqFiles.unzip)
#
# Check the values of two variables.
if (Condor) {
  # Condor will take care of this, so we just don't really worry about it.
  nStreamsDataPrep <- 9001
}

presplit <- check.presplit(paired.end,presplit)
nStreamsDataPrep <- check.nStreamsDataPrep(fqFiles,nStreamsDataPrep)
#
# Chunk the files into segments to be run in parallel.
#
# Note: This is a safe operation, nStreamsDataPrep can exceed the number
# of files and not cause a crash (It is checked above and within the function so it is modular, but explicit also)
if (presplit) {
  rangelist.Dataprep <- chunk.data.files.presplit(fqFiles,nStreamsDataPrep)
}
if (!presplit) {
  rangelist.Dataprep <- chunk.data.files.unsplit(fqFiles,nStreamsDataPrep)
}
# Copy the artificial.fq file in
if (readTrim) {
  copy.artificial.fq(artificial.fq,dest.dir,Condor)
}

# Construct command to be run.
comm.pool <- NULL
preQC <- NULL
postQC <- NULL
removeFiles <- NULL
cleanup <- NULL
for (zz in 1:nStreamsDataPrep) {
  comm.pools <- NULL
  for (j in rangelist.Dataprep[[zz]]) {

    ######################################
    # PAIRED ENDED DATA
    ######################################
    if (paired.end) {

      ######################################
      # UNSPLIT
      ######################################
      if (!presplit) {
        # Add to command pool
        if (is.null(comm.pools)) {
          comm.pools <- paste(split.unsplit.files.PE(base.dir,fqFiles[j]))
        }else{
          comm.pools <- paste(comm.pools,"&&",split.unsplit.files.PE(dest.dir,fqFiles[j]))
        }
      }

      ######################################
      # READ TRIM
      ######################################
      if (readTrim) {
        # The trim command
        trimCommand <- trimAssemble.PE(fqFiles[j], trimPath, qScores, trimhead, convertPathToName(artificial.fq),trimMin)

        if (is.null(comm.pools)) {
          comm.pools <- paste(trimCommand)
        } else{
          comm.pools <- paste(comm.pools,"&&",trimCommand)
        }

        # Modifies the names of a few files to retain naming
        file.shuffle <- file.shuffle.PE(fqFiles[j])
        comm.pools <- paste(comm.pools,"&&",file.shuffle)
        # Quality control check via Fastqc of dirty file
        if (is.null(comm.pools)) {
          preQC <- preQualityCheck.PE(fastqcPath,fqFiles[j],qcFolder,TRUE)
        } else{
          preQC <- paste(preQC,preQualityCheck.PE(fastqcPath,fqFiles[j],qcFolder,FALSE))
        }

        move.file.log <- move.paired.files.PE((fqFiles[j]),qcFolder)

        if (is.null(cleanup)){
          cleanup <- paste(move.file.log)
        } else{
          cleanup <- paste(cleanup, ";", move.file.log)
        }
      }

      ######################################
      # POST QUALITY CONTROL
      ######################################
      if (is.null(postQC)){
        postQC <- postQualityCheck.PE(fastqcPath,fqFiles[j],qcFolder, TRUE)
      } else{
        postQC <- paste(postQC,postQualityCheck.PE(fastqcPath,fqFiles[j],qcFolder, FALSE))
      }

      if (is.null(comm.pools)) {
        comm.pools <- paste("date")
      }
    }

    ######################################
    # SINGLE ENDED DATA
    ######################################
    if (!paired.end) {

      ######################################
      # READ TRIM
      ######################################
      if (readTrim) {

        # The trim command
        trimCommand <- trimAssemble.SE(fqFiles[j], trimPath, qScores, trimhead, convertPathToName(artificial.fq),trimMin)
        if (is.null(comm.pools)) {
          comm.pools <- paste(trimCommand)
        } else {
          comm.pools <- paste(comm.pools,"&&",trimCommand)
        }

        # Modifies the names of a few files to retain naming
        file.shuffle <- file.shuffle.SE(fqFiles[j])
        comm.pools <- paste(comm.pools,"&&",file.shuffle)
        # Quality control check via Fastqc of dirty file
        if (is.null(preQC)){
          preQC <- preQualityCheck.SE(fastqcPath,fqFiles[j],qcFolder,TRUE)
        } else{
          preQC <- paste(preQC,preQualityCheck.SE(fastqcPath,fqFiles[j],qcFolder,FALSE))
        }

        remove.command <- remove.unneeded.files.SE(fqFiles[j])
        move.file.log <- move.paired.files.SE((fqFiles[j]),qcFolder)
        indiv.cleanup.files <- paste(remove.command,";",move.file.log)
        if (is.null(cleanup)){
          cleanup <- paste(indiv.cleanup.files)
        } else{
          cleanup <- paste(cleanup, ";", indiv.cleanup.files)
        }
      }

      ######################################
      # POST QUALITY CONTROL
      ######################################
      if (is.null(postQC)){
        postQC <- postQualityCheck.SE(fastqcPath,fqFiles[j],qcFolder,TRUE)
      } else{
        postQC <- paste(postQC,postQualityCheck.SE(fastqcPath,fqFiles[j],qcFolder,FALSE))
      }

      if (is.null(comm.pools)) {
        comm.pools <- paste("date")
      }
    }
  }

  ######################################
  # Make it run in the background
  ######################################
  comm.pools <- paste(comm.pools,"&")

  # Push the different jobs onto the command stack.
  if (is.null(comm.pool)) {
    comm.pool <- paste(comm.pools)
  } else {
    comm.pool <- paste(comm.pool,comm.pools)
  }

}

# Store stuff
store.artificial <- store.artificial.seqs.file(paste(dest.dir,convertPathToName(artificial.fq),sep=""),qcFolder)
comm.pool <- paste(comm.pool,"wait","&&", store.artificial)


# We don't use the QC to actually make any decisions currently.  So let's put it at the end here so it can be run in parallel
# When not using Condor.  I might want to code up a solution to allow Condor to parallelize this.
if (!is.null(preQC)){
  comm.pool <- paste(comm.pool,"&&",preQC)
}

if (!is.null(cleanup)){
  comm.pool <- paste(comm.pool,"&&",cleanup)
}

if (!is.null(postQC)){
  comm.pool <- paste(comm.pool,"&&",postQC)
}
printOrExecute(comm.pool,Condor)