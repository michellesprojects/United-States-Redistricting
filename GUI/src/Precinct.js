class Precinct{
    constructor(p){
        this.center = [p.centerX, p.centerY];
        this.comments = p.comments;
        this.geoid = p.geoId;
        this.precinctName = p.precinctName;
        this.neighbors = []
        p.precinctNeighbors.forEach(n => {
            this.neighbors.push(n.neighborGeoid)
        })
        this.precinctType = p.precinctType;
        this.records = p.records;
        this.shapeType = p.shapeType;
        this.stateName = p.stateName;
        this.type = p.ptype;
        this.demographicData = p.demographicData;
        this.electionData = p.electionDataCollection;
    }

    setNeighbors = (s) => {
        this.neighbors = s.split(',');
    }
}

setPrecinctLayerHandlers = (feature, layer) => {
    precinctLayersMap[feature.properties.geoid] = layer;
    statePrecinctsGroup.addLayer(layer);
    let polygon = layer
    polygon.on('mouseover', function(){
        if (selectedPrecinctLayer != null){
            if (selectedPrecinctLayer.polygon == polygon){return}
        }
        let tempStyle = JSON.parse(JSON.stringify(polygon.options));
           let newStyle = mouse_over_style;
        polygon.setStyle(newStyle);
        tempOldLayerStyle = tempStyle;
    });
    polygon.on('mouseout', function () {
        if (selectedPrecinctLayer != null){
            if (selectedPrecinctLayer.polygon != polygon){
                polygon.setStyle(tempOldLayerStyle);
            }
        }
    })
    polygon.on('click', async (event) => {
        //mymap.fitBounds(polygon.getBounds(), { padding: [100, 100] });
        let geoid =feature.properties.geoid
        
        selectPrecinct(geoid)
    });

    polygon.setStyle(regular_style)
}

selectPrecinct = async (geoid) =>{
    
    highlightPolygon(null,null); //set previous colored polygon back
    await getPrecintFromServer(geoid);
        // center the view on clicked precinct
    let lan = selectedPrecinctObject.center[0];
    let lng = selectedPrecinctObject.center[1];
    //mymap.flyTo([lan,lng]);
    
    // update style on precinct when it is clicked on
    updateStyleSelectedPrecinct(geoid);   
    highlightNeighbors();
    enablePrecinctTools();
    updateCommentsList(selectedPrecinctObject);
    mymap.flyToBounds(selectedPrecinctLayer.polygon.getBounds(), {padding: [100,100]});
    if (previousEditable != selectedPrecinctLayer.polygon && previousEditable != null){
        previousEditable.pm.disable(); 
        inEdit = false
    }
        
    showPrecinctData();
    
}

getPrecintFromServer = async (geoid) => {
    let precinct = await new Promise(function(resolve, reject){
        let xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function(){
            if (xhttp.readyState == 4 && this.status == '200' && this.response != ''){
                resolve(JSON.parse(this.response))
            }
        }
        xhttp.open("GET", 'http://localhost:8080/precinct?geoid='+geoid)
        xhttp.send();
    })
    selectedPrecinctObject = new Precinct(precinct);
}

highlightNeighbors = () => {
    /* Dehighlight Old neighbors */
    if (highlitedNeighbors != ''){
        highlitedNeighbors.forEach(element => {
            let x = precinctLayersMap[element.trim()];
            if (x && element != selectedPrecinctObject.geoid)
                x.setStyle(regular_style) 
        });
    }
    highlitedNeighbors = [];
    /* Highlight new neighbors */
    selectedPrecinctObject.neighbors.forEach(element => {
        let x = precinctLayersMap[element.trim()];
        if (x){
            highlitedNeighbors.push(element);
            x.setStyle(neighbor_style) 
        }
    });
}

/**
 * params = {master, slave, comment}
 */
removeNeighborHTTPrequest = async (params) => {
    let promise =  new Promise(function(resolve, reject){
        let objectToSend = {'master': params.master, 'slave' : params.slave, 'comment': params.comment}
        let xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/precinct/remove/neighbor')
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onload = function() {
            if (xhr.readyState == 4 && this.status == 200) {
                if (this.responseText == "")
                    M.toast({html: 'Failed to remove neighbor, try again', classes: 'rounded'});
                else{
                    M.toast({html: 'Precinct neighbor removed', classes: 'rounded'});
                    resolve(this.responseText);
                }
                    
            }
        }
        xhr.send(JSON.stringify(objectToSend))
    })  
    let newNeighbors = await promise;
    selectedPrecinctObject.setNeighbors(newNeighbors)
    highlightNeighbors();
    handleRemoveNeighbor(null)
}

/**
 * params = {master, slave, comment}
 */
addNeighborHTTPrequest = async (params) => {
    let promise =  new Promise(function(resolve, reject){
        let objectToSend = {'master': params.master, 'slave' : params.slave, 'comment': params.comment}
        let xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/precinct/add/neighbor')
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onload = function() {
            if (xhr.readyState == 4 && this.status == 200 ) {
                if (this.responseText == "")
                    M.toast({html: 'Failed to add neighbor, check GeoID', classes: 'rounded'});
                else {
                    M.toast({html: 'Precinct neighbor added', classes: 'rounded'});
                    resolve(this.responseText);
                }
               
            }
        }
        xhr.send(JSON.stringify(objectToSend))
    })  
    let newNeighbors = await promise;
    selectedPrecinctObject.setNeighbors(newNeighbors)
    highlightNeighbors();
    handleAddNeighbor();
}

/*GEOID1 = master precinct, GEOID2 will be added to master*/
mergePrecincts = async (params) => {
    geoid1 = params.master;
    geoid2 = params.slave;
    let masterLayer = precinctLayersMap[geoid1];
    let slaveLayer = precinctLayersMap[geoid2];
    let p1 = turf.multiPolygon(masterLayer.feature.geometry.coordinates);
    let p2 = turf.multiPolygon(slaveLayer.feature.geometry.coordinates);
    //buffers of 100feet each
    let masterBuffer = turf.buffer(p1,0.03,{units: 'kilometers'});
    let slaveBuffer = turf.buffer(p2,0.03,{units: 'kilometers'});
    let unionPolygon = turf.union(masterBuffer, slaveBuffer);
    
    //unbuff result
    unionPolygon = turf.buffer(unionPolygon,-0.03,{units: 'kilometers'});
    unionPolygon = turf.truncate(unionPolygon, {'presion': 5});
    /* If resulted union is a Polygon, need to convert it to MultiPolygon, to keep it consistent with server and DB */
    if (unionPolygon.geometry.type == 'Polygon'){
        unionPolygon.geometry.type = 'MultiPolygon';
        let arr = [];
        arr.push(unionPolygon.geometry.coordinates);
        unionPolygon.geometry.coordinates = arr;
    }

    mymap.removeLayer(masterLayer);
    mymap.removeLayer(slaveLayer);

    params.newGeometry = unionPolygon.geometry
    let updated_precinct = await mergePrecinctsHTTPrequest(params);
    selectedPrecinctObject = new Precinct(updated_precinct);
    
    let newGeoJSONFeature = {'type':'Feature','properties':{'geoid':geoid1}, 'geometry':unionPolygon.geometry}
    L.geoJson(newGeoJSONFeature, {onEachFeature : setPrecinctLayerHandlers});
    delete  precinctLayersMap[geoid2];
    masterLayer = precinctLayersMap[geoid1]
    highlightNeighbors();
    masterLayer.setStyle(selected_style);
    
    selectedPrecinctLayer.polygon = masterLayer;
}

mergePrecinctsHTTPrequest = (params) =>{
    return new Promise(function(resolve, reject){
        let obj_to_send = {'state': selectedStateAbbr.toUpperCase(),'master': params.master, 'slave' : params.slave, 
            'new_geometry': JSON.stringify(params.newGeometry), 'comment': params.comment};
        let xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/precinct/merge', true)
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onload = function() {
            if (xhr.readyState == 4 && this.status == 200) {
                let response = JSON.parse(this.responseText);
                if (response != null){
                    M.toast({html: 'Precincts merged', classes: 'rounded'});
                    resolve(response)
                }
            }
        }
        xhr.send(JSON.stringify(obj_to_send))
    })  
}


updateBoundariesHTTPrequest = async (geoid, shape) => {
    let promise =  new Promise(function(resolve, reject){
        let objectToSend = {'geoid': geoid, 'shape' : JSON.stringify(shape)}
        let xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/precinct/update/shape')
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onload = function() {
            if (xhr.readyState == 4 && this.status == 200 && this.responseText) {
                if (this.responseText == 'true'){
                    M.toast({html: 'Precinct boundaries updated', classes: 'rounded'});
                } else {
                    M.toast({html: 'Precinct boundaries update failed, try again', classes: 'rounded'});
                }
            }
        }
        xhr.send(JSON.stringify(objectToSend))
    })  
    let result = await promise;
    
}

/** 
 * @param {Object} params = {geoid, comment}
 */ 
defineGhost = async (params) => {
    let promise =  new Promise(function(resolve, reject){
        let objectToSend = {'master': params.master, 'comment': params.comment}
        let xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/precinct/defineGhost')
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onload = function() {
            if (xhr.readyState == 4 && this.status == 200 ) {
                if (this.responseText == "")
                    M.toast({html: 'Failed to define precinct as Ghost, try again', classes: 'rounded'});
                else {
                    M.toast({html: 'Precinct defined as Ghost', classes: 'rounded'});
                    selectedPrecinctObject = JSON.parse(this.responseText);
                    selectPrecinct(selectedPrecinctObject.geoid);
                }
               
            }
        }
        xhr.send(JSON.stringify(objectToSend))
    })  
    await promise;
}