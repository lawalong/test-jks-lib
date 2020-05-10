import groovy.io.FileType

def call(Map params) {

  // Requiered parameters
  /*
  appName           = params.appName
  appProject        = params.appProject
  appVersion        = params.appVersion
  technology        = params.technology
  unitTests         = params.unitTests
  runSonarAnalysis  = params.runSonarAnalysis
  devLocations      = params.devLocations
  liveAutoDeploy    = params.liveAutoDeploy
  liveLocations     = params.liveLocations
  soapUI            = params.soapUI
  soapUIScripts     = params.soapUIScripts
  soapUISecrets     = params.soapUISecrets
  customChartName   = params.customChartName
  
  continuousIntegrationImage = sonar.getSonarScannerImage(technology)
    repositoryBranch            = "master"
  repositoryUrl               = "https://github.com/Webjet/Packages-Web.git"
  */


  repositoryBranch            = params.repositoryBranch
  repositoryUrl               = params.repositoryUrl
  dockerFilePath              = params.dockerFilePath
  dockerImages                = params.dockerImages
  forTest = ["1","2","3","ab"]


  
  pipeline {
    agent any
    environment {
        CONTAINERREGISTRY         = credentials('9bfddad9-c9b4-4f57-b6cc-283d0997c5b5')
        CONTAINERREGISTRYUSERNAME = credentials('ac3722c7-104f-4835-8438-70042e18a846')
        CONTAINERREGISTRYPASSWORD = credentials('399bf241-b44f-4977-84c1-551792f726df')
        def WORKSPACE=pwd()
    }
    stages{
      
      stage('checkout') {
          steps{
              checkout([$class: 'GitSCM', branches: [[name: '*/'+repositoryBranch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'f136133b-0c9f-4d5b-ba68-6d9d1fab21b6', url: repositoryUrl]]])
          }          
      }

      stage('loging into container registry') {
          steps{
              sh 'docker login -u $CONTAINERREGISTRYUSERNAME -p $CONTAINERREGISTRYPASSWORD $CONTAINERREGISTRY'

          }
      }
      
      stage('build docker image') { // need update, for, image name
          steps{
            sh 'cd $WORKSPACE/${dockerFilePath}'
            script{                
                dockerImages.each{
                  echo "${it['args']}"
                  sh 'docker build --build-arg REGISTRY=containerregistrydev.azurecr.io -t "${CONTAINERREGISTRY}/webjet/alertbot":$BUILD_NUMBER .'
                }
            }
            
          }
      }
    

/*
docker build ${it[1]} -t "${CONTAINERREGISTRY}/webjet/${it[0]}":$BUILD_NUMBER .
docker build it['args'] -t "${CONTAINERREGISTRY}/webjet/it['imageName']":$BUILD_NUMBER .

      stage('push image') { // need update, image name
          steps{
            sh(script: """
                docker push "${CONTAINERREGISTRY}/webjet/${dockerImageName}:${BUILD_NUMBER}"
            """, returnStdout: true)
          }
      }

      stage('deploy-kubernetes-dev'){  // need update parallel for loop, config file name, service name, namespace
            steps{
                parallel(
                    AU:{
                        sh '''
                            response=$(curl -s -X POST "http://kubebot.default/deploy/dev/bots/alertbot/${BUILD_NUMBER}?registry=$CONTAINERREGISTRY&repository=webjet" \
                            --data-binary "@$WORKSPACE/pipeline/deploy.yaml" \
                            -H 'Content-Type: application/yaml' \
                            -H 'Expect:' \
                            -D -)
                            http_status=$(echo $response | grep HTTP | awk '{print $2}')
                            if [ $http_status = 200 ]; then
                                echo "Deployed"
                            else
                                echo "Something went wrong with the deployment, query the Kb-Trace-Id in sumo for more details."
                                exit 1
                            fi      
                        '''   
                    },
                    NZ:{
                        sh '''
                            echo "test path2"    
                        ''' 
                    }
                )
            }
      }*/     


    }
  }


  /*  stages {
      stage('Continuous Integration'){
		when {
			not {
				expression {continuousIntegrationImage == "" }
			}
			beforeAgent true
		}
        agent { 
          docker { 
            image continuousIntegrationImage
            registryUrl 'https://webbeds.azurecr.io'
            registryCredentialsId 'acrcredentials' 
          }
        }
        steps {
          script {
             sonar.analyze(technology, appName,appVersion,SONAR_IP,runSonarAnalysis,unitTests)
          }
        }
      }

      stage('Build Artifact'){
        when{
          branch "master"
        }
		    steps{
          script{
            dockerUtils.createImage(appName,appProject,appVersion,false)
          }
        }
      }
      stage('Update Docker Image TAG in DEV'){
        when{
          branch "master"
        }
        options { skipDefaultCheckout() }
		    steps{
          script{
            devLocations.each{
              kubernetes.upgradeChartVersion(appName,appVersion,"${it}","dev",false,customChartName)
            }
          }
        }
       } 
      stage('Deploy new version in DEV'){
        agent {
          docker {
            image 'webbeds.azurecr.io/argo/argocd:1.3.6'
            registryUrl 'https://webbeds.azurecr.io'
            registryCredentialsId 'acrcredentials'
          }
        }
        options {
          timeout(time: 1, unit: 'HOURS') 
        }
		    when{
          branch "master"
        }
        steps{
          script{
           def stepsForParallel = [:]
           devLocations.each{
            def stepName = "Deploying in ${it}"
            stepsForParallel[stepName] = { -> 
              kubernetes.argoSync(appName,ARGO_IP,"${it}","dev") 
            }
           }
          parallel stepsForParallel
          }
        }
      }

	  stage('Integration Tests'){
		when {
			expression { soapUI == true }
		}
		agent {
		  docker {
			image "webbeds.azurecr.io/soapui/soapui:latest"
			registryUrl 'https://webbeds.azurecr.io'
			registryCredentialsId 'acrcredentials'
		  }
		}
		steps {
		  script {
        if (soapUISecrets) {
          withCredentials([file(credentialsId: soapUISecrets, variable: 'fileName')]) {
			      soapui.runTests(soapUIScripts, fileName, 100)
          }
        } else {
			    soapui.runTests(soapUIScripts, 100)
        }
		  }
		}
		post {
		  always {
			archiveArtifacts 'soapuireports/*'
			junit 'soapuireports/TEST-*.xml'
		  }
		}
	  }

      stage('Update Docker Image TAG in LIVE'){
        when{
          branch "master"
        }
        options { skipDefaultCheckout() }
        steps{
          script{
            // Before update we upload the live image to dockerRegistry
            dockerUtils.createImage(appName,appProject,appVersion,true)
            liveLocations.each{
              kubernetes.upgradeChartVersion(appName,appVersion,"${it}","live",true,customChartName)
            }
          }
        }
      }
      stage('Deploy new version in LIVE'){
        agent {
          docker {
            image 'webbeds.azurecr.io/argo/argocd:1.3.6'
            registryUrl 'https://webbeds.azurecr.io'
            registryCredentialsId 'acrcredentials'
          }
        }
        options {
          timeout(time: 1, unit: 'HOURS') 
        }
        when{
          branch "master"
        }
        steps{
         script{
           if (liveAutoDeploy){
            def stepsForParallel = [:]
            liveLocations.each{
            def stepName = "Deploying in ${it}"
            stepsForParallel[stepName] = { -> 
              kubernetes.argoSync(appName,ARGO_IP,"${it}","live") 
            }
           }
           parallel stepsForParallel 
          }else{
            echo "liveAutoDeploy disabled. Go to https://argocd.webbeds.com/applications?proj=${appProject} to Deploy manually"
          }
         }
        }
      }
    }      
    post {
      always {
        echo 'Clean up workspace...'
        deleteDir()
      }
    }
  }*/



}
