class Errors{
    constructor(errors, errorType){
        this.errors = errors
        this.errorType =errorType
    }
}


getErrorByTypeFromServer = async (errorType) => {
    let errors = await new Promise(function(resolve, reject){
        let url = 'http://localhost:8080/errors?state=' + selectedStateObject.name + '&type=' + errorType
        let xhttp = new XMLHttpRequest();
        //let obj = {state: selectedStateObject.name, precinct: selectedPrecinctObject.geoid, errorType: errorType};
        xhttp.onreadystatechange = function(){
            if (xhttp.readyState == 4 && this.status == '200' && this.response != ''){
                resolve(JSON.parse(this.response))
            }
        }
        xhttp.open("GET", url)
        xhttp.send();
    })
    selectedErrors = new Errors(errors,errorType);
}

deleteErrorById = () => {
    let url = 'http://localhost:8080/errors/delete?id=' + selectedErrorToDelete;
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if (xhttp.readyState == 4 && this.status == '200' && this.response != ''){
                console.log('Deleted')
                $('#list-error-' + selectedErrorToDelete).remove()
                /*
                let i = 0;
                for (i;i < selectedErrors.errors.length;i++){
                    if (selectedErrors.errors[i].errorId == selectedErrorToDelete)
                        break;
                }
                selectedErrors.errors.splice(i,1);
                */
                handleErrorTypeSelect(null, selectedErrorType);
            }
        }
    xhttp.open("GET", url)
    xhttp.send();
}

showErrorPrecincts = () =>{
    dehighlighErrors();
    let errors = selectedErrors.errors;
    errors.forEach(x => {
        let geoid = x.precinct.geoId;
        highlightedErrorsGroup.push(precinctLayersMap[geoid])
        precinctLayersMap[geoid].setStyle(precinct_error_style)
    })
    $('#error-list-ul').html('');
    generateErrorListItems();
}

showGapsOnMap = () =>{
    dehighlighErrors();
    $('#error-list-ul').html('');
    generateNonPrecinctErrorListItems();
}

showAnomaly = () => {

}
