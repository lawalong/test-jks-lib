def deploy(region,environment,nameSpace,appName) {
    echo "333 Deploying ${appName}-wjau to ${nameSpace} ..."

    sh '''
    echo \"4442 Deploying '''+appName+''' -wjau to '''+nameSpace+''' ...\"
    local url = http://kubebot.default/deploy/dev/'''+nameSpace+'''/'''+appName+'''/${BUILD_NUMBER}?registry=$CONTAINERREGISTRY&repository=webjet
    echo url


                                response=$(curl -s -X POST "$url" \
                            --data-binary "@$WORKSPACE/pipeline/deploy.yaml" \
                            -H 'Content-Type: application/yaml' \
                            -H 'Expect:' \
                            -D -)

                            echo "$response"
    '''


}
