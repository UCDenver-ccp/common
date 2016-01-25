# common
a library of utility code for a variety of "common" tasks

## Development
This project ascribes to the Git-Flow approach to branching as originally described [here](http://nvie.com/posts/a-successful-git-branching-model/). 
To facilitate the Git-Flow branching approach, this project makes use of the [jgitflow-maven-plugin](https://bitbucket.org/atlassian/jgit-flow) as described [here](http://george-stathis.com/2013/11/09/painless-maven-project-releases-with-maven-gitflow-plugin/).

Code in the [master branch](https://github.com/UCDenver-ccp/common/tree/master) reflects the latest release of this library. Code in the [development](https://github.com/UCDenver-ccp/common/tree/development) branch contains the most up-to-date version of this project.

## Maven signature
```xml
<dependency>
	<groupId>edu.ucdenver.ccp</groupId>
	<artifactId>common</artifactId>
	<version>1.5.1</version>
</dependency>


<repository>
	<id>bionlp-sourceforge</id>
	<url>http://svn.code.sf.net/p/bionlp/code/repo/</url>
</repository>
```