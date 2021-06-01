handleShowPrecinctsCheckbox = () => {
  if($("#show_pr_cb").is(":checked"))
    displayStatePrecincts(selectedStateJSON);
  else 
    hideStatePrecincts();
}
handleShowCDCheckbox = () => {
  if($("#show_cd_cb").is(":checked"))
    showCongDist(selectedStateAbbr);
  else 
    hideCongDist(selectedStateAbbr);
}

handleShowNPCheckbox = () => {
  if($("#show_np_cb").is(":checked"))
    showNationalParks(selectedStateAbbr);
  else 
    hideNationalParks(selectedStateAbbr);
}

function handleAddComment(btn) {
  if (selectedPrecinctLayer.polygon == null){
    postCommentToState();
  }else{
    postCommentToPrecinct();
  }
}

function dropdownHandler() {
    let dropdown_value = $("#states_selection_dropdown").val();
    if (dropdown_value=='none'){
      showAllStates();
    } else {
      selectedStateAbbr = dropdown_value.substring(0,2);
      reset_State_Change();
      $("#states_selection_dropdown").val(dropdown_value);
      showExactState(selectedStateAbbr);
    }
}

handleAddNeighbor = () => {

  let edit_div = document.getElementById("neighbor_edit_div");
  edit_div.innerHTML = ''
  edit_div.appendChild(document.createElement('br'))

  //create label
  let label = document.createElement("label");
  label.innerHTML = 'Enter ID:'
  edit_div.appendChild(label)
  edit_div.appendChild(document.createElement('br'))

  //create text area to enter geoid
  let add_neighbor_div = document.createElement("input");
  add_neighbor_div.id = 'add_neighbor_ta'
  add_neighbor_div.setAttribute("type", "text");
  add_neighbor_div.setAttribute("value", "");
  edit_div.appendChild(add_neighbor_div);

  //create add button
  let add_btn = createElem('button', 'btn-small','Add' ,'Add')
  add_btn.addEventListener('click', function (event) {
    let temp = document.getElementById('add_neighbor_ta')
    let new_neighbor_geoid = temp.value.trim();
    if (selectedPrecinctObject.neighbors.includes(new_neighbor_geoid) || new_neighbor_geoid == selectedPrecinctObject.geoid){
      return;
    }
    showModalCorrectionComment(addNeighborHTTPrequest, {master: selectedPrecinctObject.geoid, slave: new_neighbor_geoid});
  })

  let cancel_btn = createElem('button', 'btn-small','Cancel' ,'Cancel')
  cancel_btn.addEventListener('click', ()=>{
    document.getElementById("neighbor_edit_div").innerHTML = '';
    highlightPolygon(null,null)
  })

  edit_div.appendChild(document.createElement('br'))
  edit_div.appendChild(add_btn);
  edit_div.appendChild(cancel_btn)
}

handleRemoveNeighbor = () => {
   let edit_div = document.getElementById("neighbor_edit_div");
   edit_div.innerHTML = ''
   edit_div.appendChild(document.createElement('br'))
   let selectContainer = createElem('div', 'input-field col', '', '')

   let select = document.createElement("select");
   select.id = 'select_neighbor_remove'
   
  select.onchange = function () {
    highlightPolygon($('#select_neighbor_remove').val(), selected_to_operate);
  }
  
   selectedPrecinctObject.neighbors.forEach(element => {
      let opt = createElem('option', '' , element, element);
      select.appendChild(opt)
   });
   selectContainer.appendChild(select);
   
   let removeBtn = createElem('button', 'btn-small', 'Remove', '')
   removeBtn.addEventListener('click', function (event) {
      let temp = document.getElementById('select_neighbor_remove')
      let selected = temp.options[temp.selectedIndex].text;
      showModalCorrectionComment(removeNeighborHTTPrequest, {master: selectedPrecinctObject.geoid, slave: selected});
      
   })

   let cancelBtn = createElem('button', 'btn-small', 'Cancel', '')
   cancelBtn.addEventListener('click', ()=>{
      document.getElementById("neighbor_edit_div").innerHTML = '';
      highlightPolygon(null,null)
   })

  $(document).ready(function(){
    $('select').formSelect();
  });

  edit_div.appendChild(selectContainer);
  edit_div.appendChild(document.createElement('br'))
  edit_div.appendChild(removeBtn)
  edit_div.appendChild(cancelBtn)
}

handleMergePolygons = () => {
  let edit_div = document.getElementById("merge_edit_div");
  edit_div.innerHTML = ''
  edit_div.appendChild(document.createElement('br'))

   // create SELECT element and fill with GEOID of neighbors
  let selectContainer = createElem('div', 'input-field col', '', '')
  let select = document.createElement("select");
  select.id = 'merge_neighbor_select'

  select.onchange = function () {
    highlightPolygon($('#merge_neighbor_select').val(), selected_to_operate);
  }
   
  selectedPrecinctObject.neighbors.forEach(element => {
      let opt = createElem('option', '' , element, element);
      select.appendChild(opt)
  });
  selectContainer.appendChild(select);

  let mergeBtn = createElem('button', 'btn-small', 'Merge', '')
  mergeBtn.addEventListener('click', function (event) {
    let temp = document.getElementById('merge_neighbor_select')
    let selected = temp.options[temp.selectedIndex].text;
    //mergePrecincts(selectedPrecinctObject.geoid, selected)
    showModalCorrectionComment(mergePrecincts, {master: selectedPrecinctObject.geoid, slave: selected});
  })
  
  let cancelBtn = createElem('button', 'btn-small', 'Cancel', '')
  cancelBtn.addEventListener('click', ()=>{
      $("#merge_edit_div").html('');
      highlightPolygon(null,null);
  })
  
  $(document).ready(function(){
    $('select').formSelect();
  });

  edit_div.appendChild(selectContainer);
  edit_div.appendChild(document.createElement('br'))
  edit_div.appendChild(mergeBtn)
  edit_div.appendChild(cancelBtn)

}

reset_State_Change = () => {
  selectedPrecinctObject = null;
  selectedStateObject = null
  selectedPrecinctLayer =  {oldStyle:null, polygon:null};
  editablePrecinctLayer = null;
  highlitedNeighbors = []
  inEdit = false;
  previousEditable = null;
  precinctLayersMap = [];
}

handleErrorTypeSelect = async (node, errorType) =>{
  let errType;
  if (errorType){
    errType = errorType
    await getErrorByTypeFromServer(errType)
  }else{
    errType= node.dataset['value']
    await getErrorByTypeFromServer(errType)
  }
  switch (errType) {
    case 'enclosed':
      selectedErrorType = errType
      showErrorPrecincts();
      break;
    case 'multipolygon':
      selectedErrorType = errType
      showErrorPrecincts();
      break;
    case 'overlap':
      selectedErrorType = errType
      showErrorPrecincts();
      break;
    case 'coverage':
      showGapsOnMap();
      break
    case 'anomalous':
      showErrorPrecincts();
      break
    default:
      break;
  }
}

handleSaveNewBoundary = () =>{
  updateBoundariesHTTPrequest(selectedPrecinctObject.geoid, newEditedShape.geometry);
}

handleDefineGhost = () => {
  let confirm = 'You are about to define this precinct as Ghost. All demographic and election data will be erased.\nIf you still want to process, leave a comment and press "Yes"'
  showModalCorrectionComment(defineGhost, {master: selectedPrecinctObject.geoid}, confirm);
}