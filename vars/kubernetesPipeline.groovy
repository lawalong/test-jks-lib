import groovy.io.FileType

def call() {
  
  pipeline {
    agent any

    stages {
      stage('Continuous Integration'){

        steps {
          echo "1";
        }
      }

      stage('Build Artifact'){
		    steps{
          echo "2";
        }
      }

      stage('Update Docker Image TAG in DEV'){
		    steps{
          echo "3";
        }
       } 
      stage('Deploy new version in DEV'){
		    steps{
          echo "4";
        }
      }

	  stage('Integration Tests'){
		    steps{
          echo "5";
        }
		}
    }
    
  }
}
