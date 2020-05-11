def deploy(region,environment,nameSpace,appName) {
    echo "333 Deploying ${appName}-wjau to ${nameSpace} ..."

    sh '''
    echo \"4442 Deploying '''+appName+''' -wjau to '''+nameSpace+''' ...\"



                            response=$(curl -s -X POST "http://kubebot.default/deploy/dev/'''nameSpace'''/'''appName'''-wjnz/${BUILD_NUMBER}?registry=$CONTAINERREGISTRY&repository=webjet" \
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
}
