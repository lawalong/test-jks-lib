import groovy.io.FileType

def call() {
  
  pipeline {
    agent any

    stages {
      stage('Continuous Integration'){

        steps {
          echo 1;
        }
      }

      stage('Build Artifact'){
		    steps{
          echo 1; 
        }
      }

      stage('Update Docker Image TAG in DEV'){
		    steps{
          echo 1; 
        }
       } 
      stage('Deploy new version in DEV'){
		    steps{
          echo 1; 
        }
      }

	  stage('Integration Tests'){
		    steps{
          echo 1; 
        }
		}
    }
    
  }
}
