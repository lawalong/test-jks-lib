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

  appName                     = params.appName
  nameSpace                   = params.nameSpace
  repositoryBranch            = params.repositoryBranch
  repositoryUrl               = params.repositoryUrl
  dockerFilePath              = params.dockerFilePath
  dockerImages                = params.dockerImages
  deployRegions               = params.deployRegions
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
      
      stage('build docker image') { 
          steps{
            
            script{                
                dockerImages.each{
                  dockerUtils.buildImage(it['imageName'],it['args'],dockerFilePath)
                }
            }
            
          }
      }
    
      stage('push image') { 
          steps{
            script{                
                dockerImages.each{
                  dockerUtils.pushImage(it['imageName'])
                }
            }
            echo "${DEPLOY_TO_PROD}"
          }
      }

      stage('deploy-kubernetes-dev'){  
            steps{
                parallel(
                    AU:{
                      script{
                       if(deployRegions['AU']){
                         echo "Deploying ${appName}-wjau to ${nameSpace} ..."
                         kubebotUtils.deploy("wjau",'dev','deploy.yaml',nameSpace,appName)
                       }
                      }     
                    },
                    NZ:{
                      script{
                       if(deployRegions['NZ']){
                         echo "Deploying ${appName}-wjnz to ${nameSpace} ..."
                         kubebotUtils.deploy("wjnz",'dev','deploy.yaml',nameSpace,appName)
                       }              
                      }
                    }
                )
            } // steps
      }    

      stage('deploy-kubernetes-prod'){  
            steps{
                parallel(

                    AU:{
                      script{
                       if(deployRegions['AU'] && DEPLOY_TO_PROD == true){
                         echo "Deploying ${appName}-wjau to ${nameSpace} ..."
                         kubebotUtils.deploy("wjau",'dev','deploy-prod.yaml',nameSpace,appName)
                       }
                      }     
                    },
                    NZ:{
                      script{
                       if(deployRegions['NZ'] && DEPLOY_TO_PROD == true){
                         echo "Deploying ${appName}-wjnz to ${nameSpace} ..."
                         kubebotUtils.deploy("wjnz",'dev','deploy-prod.yaml'nameSpace,appName)
                       }              
                      }
                    }
                )
            } // steps
      }  


    }
  }




}
