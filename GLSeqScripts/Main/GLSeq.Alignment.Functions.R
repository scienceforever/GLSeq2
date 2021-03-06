source("GLSeq.Util.R")

####################################
# Copy genome indices to the destination dir:
####################################
copy.genome <- function(refFASTAname,dest.dir) {
  if (is.null(refFASTAname) || is.null(dest.dir)) stop("Arguments should not be NULL")
  dest.dir <- trailDirCheck(dest.dir)
  indCopy <- paste("cp", refFASTAname,dest.dir)
}

countable.sam.name <- function(this.resName) {
  if(is.null(this.resName)) stop("Arguments should not be NULL")
  countable.sam <- paste(this.resName, "countable.sam", sep=".")
  countable.sam
}

assign.resName <- function(name,text.add) {
  if (is.null(name) || is.null(text.add)) stop("Arguments should not be NULL")
  this.resName <- paste(name, text.add, sep=".")
  this.resName
}

assign.name <- function(file,paired.end) {
  if(is.null(file) || is.null(paired.end)) stop("Arguments should not be NULL")
  if(!is.logical(paired.end)) stop("Paired end must be a LOGICAL")
  name <- file
  if (paired.end){
    name <- substr(name,1,(nchar(name) - 5))
  } else{
    name <- substr(name,1,(nchar(name) - 3))
  }
  name
}