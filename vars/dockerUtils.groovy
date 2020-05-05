def ttcall() {
  
  pipeline {
    agent any

    stages {
      stage('d1'){

        steps {
          echo "1";
        }
      }

      stage('d2'){
		    steps{
          echo "2";
        }
      }


    }
    
  }
}


