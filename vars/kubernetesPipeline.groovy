import groovy.io.FileType

def call(Map params) {

  // Requiered parameters
  appName                     = params.appName
  nameSpace                   = params.nameSpace
  repositoryBranch            = params.repositoryBranch
  repositoryUrl               = params.repositoryUrl
  dockerFilePath              = params.dockerFilePath
  dockerImages                = params.dockerImages
  deployRegions               = params.deployRegions


  
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
/*
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
          }
      }
*/
      stage('deploy-kubernetes-dev'){  
            steps{
                parallel(
                    AU:{
                      script{
                        echo "flag1=" + deployRegions['AU'];
                       if(deployRegions['AU'] == true){
                         echo "Deploying ${appName}-wjau to ${nameSpace} ..."
                   //      kubebotUtils.deploy("wjau",'dev','kube-wjau-dev',nameSpace,appName)
                       }
                      }     
                    },
                    NZ:{
                      script{
                       if(deployRegions['NZ'] == true){
                         echo "Deploying ${appName}-wjnz to ${nameSpace} ..."
                     //    kubebotUtils.deploy("wjnz",'dev','kube-wjnz-dev.yaml',nameSpace,appName)
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
                        echo "flag2=" + DEPLOY_TO_PROD;
                        echo "flag3=" + deployRegions['AU'];
                       if(deployRegions['AU'] == true && DEPLOY_TO_PROD.equals("true")){
                         echo "Deploying ${appName}-wjau to ${nameSpace} Production..."
                       //  kubebotUtils.deploy("wjau",'prod','kube-wjau-prod.yaml',nameSpace,appName)
                       }
                      }     
                    },
                    NZ:{
                      script{
                       if(deployRegions['NZ'] == true && DEPLOY_TO_PROD.toBoolean() == true){
                         echo "Deploying ${appName}-wjnz to ${nameSpace} Production..."
                       //  kubebotUtils.deploy("wjnz",'prod','kube-wjau-prod.yaml',nameSpace,appName)
                       }              
                      }
                    }
                )
            } // steps
      }  


    }
  }




}
