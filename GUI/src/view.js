
/**
 * Toggles a polygon to editable or not. Use for EDIT PRECINCT button.
 */
function setPolygonEditable() {
    if (previousEditable == selectedPrecinctLayer.polygon){
        selectedPrecinctLayer.polygon.pm.disable(); 
        previousEditable = null; 
        inEdit = false;
        $("#edit_precinct_save_btn").addClass('disabled');
        return;
    }       
    if (inEdit)
        previousEditable.pm.disable();
    selectedPrecinctLayer.polygon.pm.enable({ allowSelfIntersection: false, snappable: false, snapDistance: 20}); 
    
    selectedPrecinctLayer.polygon.on('pm:edit', layer => {
        newEditedShape = layer.sourceTarget.toGeoJSON();
        let x = layer.target.toGeoJSON();

        console.log(x)
        // compare, diff & store to DB here
      });
    previousEditable = selectedPrecinctLayer.polygon;
    inEdit = true;
    $("#edit_precinct_save_btn").removeClass('disabled')
    
}

enablePrecinctTools = () => {
    $(".edit-tools").removeClass('disabled')
    $("#edit_precinct_save_btn").addClass('disabled')
}

enableStateTools = () => {
    $(".state-switch_tools").prop("disabled", false);
    $(".comment-tools").removeClass('disabled')

}

function enableAddNeighborButton() {
    document.getElementById('add_neighbor_btn').disabled = false;
}

function enableRemoveNeighborButton() {
    document.getElementById('remove_neighbor_btn').disabled = false
}

function disableAddNeighborButton() {
    document.getElementById('add_neighbor_btn').disabled = true
}

function disableRemoveNeighborButton() {
    document.getElementById('remove_neighbor_btn').disabled = true
}

function enableEditPrecinctButton() {
    $('#edit_precinct_btn').prop('disabled', false)
}

function disableEditPrecinctButton() {
    $('#edit_precinct_btn').prop('disabled', true)
}

showPrecinctData = () => {
    let dataDiv = $('#data-edit-div');
    dataDiv.html("");
    dataDiv.append(generateDataListItem('ID: '+ selectedPrecinctObject.geoid,null,null));
    dataDiv.append(generateDataListItem('Type: '+ selectedPrecinctObject.type,null,null));
    dataDiv.append(generateDataListItem('Name: '+ selectedPrecinctObject.precinctName,null,null));
    dataDiv.append(generateDataListItem('State: '+ selectedPrecinctObject.stateName,null,null));
    dataDiv.append(generateDataListItem('Population: '+ intToCommaInt(selectedPrecinctObject.demographicData.population), selectedPrecinctObject.demographicData,'&#10133'));
    selectedPrecinctObject.electionData.forEach(data => {
        dataDiv.append(generateElectionDataListItem(capitalizeFirstLetter(data.type) + ' '+ data.year, data,'&#10133'));
    })
}

updatePrecinctStyle = (geoid, style) => {
    precinctLayersMap[geoid].setStyle(style)
}

/** Keep track of selected precinct layer. 
 * @param {*} polygon - newly selected polygon */
updateStyleSelectedPrecinct = (geoid) => {
    if(selectedPrecinctLayer.polygon != null){
        selectedPrecinctLayer.polygon.setStyle(regular_style)
    }
    let polygon = precinctLayersMap[geoid]
    selectedPrecinctLayer.polygon = polygon;
    let tempStyle = polygon.options;
    selectedPrecinctLayer.oldStyle = JSON.parse(JSON.stringify(tempStyle));
    polygon.setStyle(selected_style);
}

highlightPolygon = (polygon, style) => {
    if (polygon == null){
        tempOldStyle.polygon = null
        return
    }
    polygon = polygon.trim()
    if (tempOldStyle.polygon != null){
        let oldPolygon = precinctLayersMap[tempOldStyle.polygon]
        if (oldPolygon)
            oldPolygon.setStyle(tempOldStyle.oldStyle);
    }
    
    tempOldStyle.polygon = polygon;
    let tempStyle = precinctLayersMap[polygon].options;
    tempOldStyle.oldStyle = JSON.parse(JSON.stringify(tempStyle));
    precinctLayersMap[polygon].setStyle(style);
}
  
createElem = (element, classes, innerHTML, value) =>{
    let newElement = document.createElement(element);
    newElement.className = classes;
    newElement.innerHTML = innerHTML
    newElement.value = value
    return newElement;
}

generateElectionDataListItem = (header, body,icon) => {
    let li = createElem('li','','','')
    let div = createElem('div', 'collapsible-header',header,'');
    if (icon){
        let span = createElem('span', 'badge', icon,'');
        div.appendChild(span)
    }
    li.appendChild(div)
    if (body != null){
        let bodyDiv = createElem('div', 'collapsible-body','','');
        
        let p = createElem('p','','Democrats: ' + intToCommaInt(body.democraticVotes),'');
        bodyDiv.appendChild(p); 
        p = createElem('p','','Republicans: ' + intToCommaInt(body.republicanVotes),'');
        bodyDiv.appendChild(p); 
        li.appendChild(bodyDiv)  
        let br = document.createElement("br");
        bodyDiv.appendChild(br);
        
        let a = createElem("a", '','MIT Election Results','')
        a.href = 'https://electionlab.mit.edu/data'
        bodyDiv.appendChild(a);
    }

    return li;
}

generateDataListItem = (header, body,icon) => {
    let li = createElem('li','','','')
    let div = createElem('div', 'collapsible-header',header,'');
    if (icon){
        let span = createElem('span', 'badge', icon,'');
        div.appendChild(span)
    }
    li.appendChild(div)
    if (body != null){
        let bodyDiv = createElem('div', 'collapsible-body','','');
        body = Object.entries(body)
        body.forEach(element => {
            if (element[0] != 'id' && element[0] != 'population' && element[1] != 0){
                let p;
                if (element[0].toLowerCase() == 'hispaniclatino')
                    p = createElem('p','',''+'Hispanic-Latino'+': ' + intToCommaInt(element[1]),'');
                else
                    p = createElem('p','',''+capitalizeFirstLetter(element[0])+': ' + intToCommaInt(element[1]),'');
                bodyDiv.appendChild(p); 
            }
        });
        let br = document.createElement("br");
        bodyDiv.appendChild(br);
        
        let a = createElem("a", '','US Census Bureau','')
        a.href = 'https://www.census.gov/geographies/mapping-files/time-series/geo/tiger-data.2010.html'
        bodyDiv.appendChild(a);
        li.appendChild(bodyDiv)  
    }
    return li;
}

generateErrorListItems = () =>{
    cleanAllMarkers();
    $('#error-list-ul').html('');
    let list = selectedErrors.errors;
    list.forEach(item =>{
        //<div>Alvin<a href="#!" class="secondary-content"><i class="material-icons">send</i></a></div>
        let li = createElem('li', 'blue-text collection-item','','')
        li.id = 'list-error-' + item.errorId
        let div = createElem('div', '',item.precinct.geoId,'')
        let i = createElem('i', 'secondary-content material-icons', 'delete_forever', '')
        i.dataset['errorId'] = item.errorId;
        i.dataset['errorType'] = item.errorType;
        i.dataset['precinctName'] = item.precinct.precinctName;
        i.dataset['geoid'] = item.precinct.geoId;
        i.addEventListener('click', function(target){
            target.stopPropagation();
            selectedErrorToDelete = target.currentTarget.dataset['errorId']
            showModalDeleteError(target, deleteErrorById);
        })
        div.appendChild(i);
        li.dataset['errorId'] = item.errorId;
        if (item.centerX == 0){
            if (item.precinct.centerY < item.precinct.centerX){
                li.dataset['lat'] = item.precinct.centerY;
                li.dataset['lng'] = item.precinct.centerX;
            }else{
                li.dataset['lat'] = item.precinct.centerX;
                li.dataset['lng'] = item.precinct.centerY;
            }
        }else{
            if (item.centerX < item.centerY){
                li.dataset['lat'] = item.centerX;
                li.dataset['lng'] = item.centerY;
            }else{
                li.dataset['lat'] = item.centerY;
                li.dataset['lng'] = item.centerX;
            }
           
        }
       
        markersGroup.addLayer(L.marker([li.dataset['lng'],  li.dataset['lat']]));
        li.addEventListener('click', function(target){
           mymap.flyTo([target.currentTarget.dataset['lng'], target.currentTarget.dataset['lat']], 13);
           //selectPrecinct(target.currentTarget.innerHTML)
        })
        li.appendChild(div);
        $('#error-list-ul').append(li);
    })
    mymap.addLayer(markersGroup);
}

generateNonPrecinctErrorListItems = () =>{
    cleanAllMarkers();
    $('#error-list-ul').html('');
    let list = selectedErrors.errors;
    list.forEach(item =>{
         let li = createElem('li', 'blue-text collection-item','','')
         li.id = 'list-error-' + item.errorId
         let div = createElem('div', '', item.errorId,'')
         let i = createElem('i', 'secondary-content material-icons', 'delete_forever', '')
         i.dataset['errorId'] = item.errorId;
         i.dataset['errorType'] = item.errorType;
         i.addEventListener('click', function(target){
             target.stopPropagation();
             selectedErrorToDelete = target.currentTarget.dataset['errorId']
             showModalDeleteError(target, deleteErrorById);
         })
         div.appendChild(i);
         li.dataset['errorId'] = item.errorId;
         if (item.centerX == 0){
            li.dataset['lat'] = item.precinct.centerY;
            li.dataset['lng'] = item.precinct.centerX;
         }else{
            li.dataset['lat'] = item.centerX;
            li.dataset['lng'] = item.centerY;
         }
       
         markersGroup.addLayer(L.marker([li.dataset['lng'],  li.dataset['lat']]));
         li.addEventListener('click', function(target){
            mymap.flyTo([target.currentTarget.dataset['lng'], target.currentTarget.dataset['lat']])
            //selectPrecinct(target.currentTarget.innerHTML)
         })
         li.appendChild(div);
         $('#error-list-ul').append(li);
    })
    mymap.addLayer(markersGroup);
}


showModalDeleteError = (target, yesButtonFunction) => {
    let content = $('#modal-content')
    content.html('')
    let h4 = createElem('h5', '', 'Delete Error?', '')
    content.append(h4);
    let dataset = target.currentTarget.dataset
    let text = 'Do you want to delete an error related to precinct "' + dataset['precinctName'] + '" with GeoID: ' + dataset['geoid']
    let p = createElem('p','',text,'')
    content.append(p);

    $('#modal-yes').unbind('click')
    $('#modal-yes').click(yesButtonFunction)

    $('#modal1').modal('open'); 
}

showModalCorrectionComment = (callback, parameters, headerText) => {
    let content = $('#modal-content')
    content.html('')
    let h4;
    if(headerText) 
        h4 = createElem('h5', '', headerText, '')
    else
        h4 = createElem('h5', '', 'Add comment to correction?', '')
    content.append(h4);

    let ta = createElem('input','','Comment:','')
    ta.id = 'correction-comment-ta'
    ta.setAttribute("type", "text");
    content.append(ta);

    $('#modal-yes').unbind('click')
    $('#modal-yes').click(function(){
        $('#modal1').modal('close');
        parameters.comment= $('#correction-comment-ta').val().trim()
        callback(parameters)
    })

    $('#modal-no').unbind('click')
    $('#modal-no').click(function(){
        $('#modal1').modal('close');
        parameters.comment=''
        callback(parameters)
    })
    $('#modal1').modal('open'); 

}

cleanAllMarkers = () =>{
    markersGroup.clearLayers()
}

dehighlighErrors = () => {
    highlightedErrorsGroup.forEach(layer =>{
        if (layer)
            layer.setStyle(regular_style)
    })
    highlightedErrorsGroup = []
}