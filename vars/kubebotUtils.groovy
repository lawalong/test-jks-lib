def deploy(region,environment,configYaml,nameSpace,appName) {

                        sh '''

                            response=$(curl -s -X POST "http://kubebot.default/deploy/'''+environment+'''/'''+nameSpace+'''/'''+appName+'''-'''+region+'''/${BUILD_NUMBER}?registry=$CONTAINERREGISTRY&repository=webjet" \
                            --data-binary "@$WORKSPACE/pipeline/'''+configYaml+'''" \
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


}
