import React from 'react';
import ReactDOM from 'react-dom';
import {Table, Tabs, Tab} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {CommonModal} from './../../common/common-modal'

export class PrioritiesModal extends React.Component {
  constructor(props){
    super(props)

    this.state = {isOpen:false}
    registerEvent('priorities-modal', 'open', (stateSetter) => this.setState({isOpen: true}))
    registerEvent('priorities-modal', 'close', (stateSetter) => this.setState({isOpen:false}))

    registerReaction('priorities-modal', 'mean-rep', ['got-with-priority'], ()=>this.setState({}))

    registerReaction('priorities-modal', 'layer-rep', ['partial-updated', 'partial-updated-list'], ()=>{
      fireEvent('mean-rep', 'get-with-priority')
    })

  }

  render(){
    var content = null

    if(this.state.isOpen){
      content = getConent(this)
    }

    return <CommonModal isOpen = {this.state.isOpen} cancelHandler={()=>fireEvent('priorities-modal', 'close')} title={"Priorities"} >
          {content}
      </CommonModal>
  }
}

const getConent = function(comp){
  const means = chkSt('mean-rep', 'index-with-priorites')

  if(means == null){
    fireEvent('mean-rep', 'get-with-priority')
    return 'Loading...'
  }

  const realms = chkSt('realm-rep', 'objects')

  const realmIdToMeans = {}

  means.forEach(mean => {
    if(comp.state.currentRealmId == null){
      comp.state.currentRealmId = mean.realmid
    }

    if(realmIdToMeans[mean.realmid]==null){
      realmIdToMeans[mean.realmid] = []
    }

    realmIdToMeans[mean.realmid].push(mean)
  })

  const tabs = []

  for (const [k, v] of Object.entries(realmIdToMeans)) {
    tabs.push(<Tab eventKey={k} title={realms[k].title}>{meansTabUI(v)}</Tab>)
  }

  return <div>
            <Tabs activeKey={comp.state.currentRealmId} onSelect={(e)=>handleSelectTab(comp, e)}>
                {tabs}
              </Tabs>
        </div>

}

const handleSelectTab = function(comp, e){
  comp.setState({currentRealmId: e})
}

const meansTabUI = function(means) {

  var layers = []

  means
    .filter(mean => mean.layers != null)
    .forEach(mean => layers = layers.concat(mean.layers))

  const result = []

  layers = layers.filter(l => l.priority>0)

  layers
    .sort((l1, l2) => l1.priority - l2.priority)
    .forEach(layer => {
        const meanTitle = means.filter(m => m.id == layer.meanid)[0].title
        result.push(layerTrUI(meanTitle, layers, layer))})


  return  <Table striped bordered condensed hover >
            <tbody>
              <tr>
                <td>Mean</td>
                <td>Layer</td>
                <td></td>
                <td></td>
              </tr>
              {result}
            </tbody>
            </Table>
}

const layerTrUI = function(meanTitle, layers, layer){
  return <tr id={layer.id}>
                <td>{meanTitle}</td>
                <td>Layer {layer.depth}</td>
                <td>{layer.priority}</td>
                <td>{layerControlsUI(layers, layer)}</td>
              </tr>
}

const layerControlsUI = function(layers, layer){
  return <div>
            <a href='#' onClick={()=>changePosition(layers, layer, -1)}>Up</a>/
            <a href='#' onClick={()=>changePosition(layers, layer, 1)}>Down</a>/
            <a href='#' onClick={()=>deleteFromPriority(layers, layer)}>Delete</a>
        </div>
}

const deleteFromPriority = function(layers, layer){
  layer.priority = 0
  rePrioritize(layers, layer)
  fireEvent('layer-rep', 'partial-update-list', [layers])
}

const changePosition = function(layers, layer, move){
  swapPriorities(layers, layer, move)
  fireEvent('layer-rep', 'partial-update-list', [layers])
}

const rePrioritize = function(layersSorted, layer) {
  var curPrior = 1

  layersSorted
    .filter(l => l.id != layer.id)
    .forEach(l => l.priority = curPrior++)
}

const swapPriorities = function(layers, layer, move) {
  if(layer.priority == 1 && move == -1){
    return
  }

  if(layer.priority == layers.length-1 && move == 1){
    return
  }

  layer.priority = layer.priority + move

  for(var idx in layers){
    if(layer.id == layers[idx].id){
      const curLayer = layers[parseInt(idx)+move]
      curLayer.priority = curLayer.priority + ((-1)*move)
    }
  }
}
