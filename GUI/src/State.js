class State{
    constructor(name, properties){
        this.name = name;
        this.properties = properties;
        this.comments = properties.comments;
        this.dataErrors = properties.dataErrors;
        this.electionData = properties.electionData;
        this.demographicData = properties.demographicData;
    }
    
}

showExactState = async (stateAbbr) => {
    loadPlainMap(mymap);
    selectedStateAbbr = stateAbbr;
    let state = await getStateFromServer(stateAbbr)
    let lan = state.properties.centerX;
    let lng = state.properties.centerY;
    mymap.flyTo([lan,lng], 8);
    $("#states_selection_dropdown").val(stateAbbr + '_drop_option');
    enableStateTools()
    displayStatePrecincts(state);
    statePrecinctsGroup.addTo(mymap)
    $("#show_pr_cb").prop('checked', 'true')
}

getStateFromServer = async (stateAbbr) => {
    let shown = false;
    return new Promise(function(resolve, reject){
        let xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function(){
            if (xhttp.readyState == 4 &&this.status == '200'){
                let s = this.responseText
                if (s != '' && shown == false){
                    shown = true;
                    let state = JSON.parse(s);
                    selectedStateObject = new State(state.name, state.properties);
                    selectedStateJSON = state;
                    resolve(state);   
                }
            }
        }
        xhttp.open("GET", 'http://localhost:8080/state?name='+stateAbbr, true)
        xhttp.send();  
    })   
}

displayStatePrecincts = async (param) => {
    selectStateCleanup();
    L.geoJson(param,  {onEachFeature: setPrecinctLayerHandlers});
    //load state comments
    updateCommentsList(selectedStateObject);
    document.getElementById('new_comment_add_btn').disabled = false;
}

hideStatePrecincts = () => {
    let temp = statePrecinctsGroup
    statePrecinctsGroup.clearLayers()
    statePrecinctsGrou = temp;
}

showAllStates = async () => {
    loadPlainMap(mymap)
    let stateShapes = await getAllStates()
    let statesAbbreviations = [];
    let onStateClickHandler = (feature, layer) => {
        layer.setStyle({
            weight:2, 
            color: '#000000', 
            dashArray: '', 
            fillOpacity: 0.5, 
            fillColor: getColor(feature.properties.ALAND)
        });
        stateLayersArr.push(layer)
        statesAbbreviations.push(feature.properties.STUSPS);
        layer.on('click', function(e){
            let abbreviation = feature.properties.STUSPS;
            showExactState(abbreviation);
        });
    }
    
    allStatesLayer = L.geoJson(stateShapes, {onEachFeature: onStateClickHandler})
    //allStatesLayer.setStyle(available_state) 
    allStatesLayer.addTo(mymap)

    /* Generate dropdown options */
    dropdown_inner_html = '<option value="none">Pick State</option>';
    statesAbbreviations.forEach(state => {
        dropdown_inner_html = dropdown_inner_html + '<option value="' +state+'_drop_option">' + state + '</option>'
    })
    document.getElementById('states_selection_dropdown').innerHTML = dropdown_inner_html
}

getAllStates = () =>{
    return new Promise(function(resolve, reject){
        let xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function(){
            if (xhttp.readyState == 4 && this.status == '200' && this.responseText != '' ){
                let state_shapes = JSON.parse(this.responseText)
                resolve(state_shapes);
            }
        }
        xhttp.open("GET", 'http://localhost:8080/states')
        xhttp.send(); 
    }) 
}

selectStateCleanup = () => {
    statePrecinctsGroup.clearLayers();
    highlitedNeighbors = []
    inEdit = false;
    previousEditable = null;
    precinctLayersMap = [];
    $('#data-edit-div').html('');
    cleanAllMarkers();
    $('#error-list-ul').html('');
}

getColor = (d) => {
    d = d + ''
    d = d.slice(-1);
    return d == 0 ? '#800026' :
           d == 1  ? '#BD0026' :
           d == 2  ? '#E31A1C' :
           d == 3  ? '#FC4E2A' :
           d == 4   ? '#FD8D3C' :
           d == 5   ? '#FEB24C' :
           d == 6   ? '#FED976' :
           d == 7  ? '#E31A1C' :
           d == 8  ? '#FC4E2A' :
           d == 9 ? '#800026' :
                '#800026'
}