
// Returns the sonarscanner image needed for a specific netcore TFM or technology
// The TFM codes are defined in https://docs.microsoft.com/en-ie/dotnet/standard/frameworks
def getSonarScannerImage(technology) {
  switch(technology){
    case "netcoreapp2.2":
      return "webbeds.azurecr.io/sonarqube/sonarscanner:4.6.2-netcore2.2";
      break;
    case "netcoreapp3.1": 
      return "webbeds.azurecr.io/sonarqube/sonarscanner:4.8.0-netcore3.1"
      break;
    default:
      return "";
      break;
  }    
}

def analyze() {
  echo "xxx world hello"
}	
