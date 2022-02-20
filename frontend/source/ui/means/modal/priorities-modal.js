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
    tabs.push(<Tab eventKey={k} title={realms[k].title}>{meansUI(v)}</Tab>)
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

const meansUI = function(means) {

  const result = []
  var layers = []

  means
    .filter(mean => mean.layers != null)
    .forEach(mean => layers = layers.concat(mean.layers))

  layers
    .filter(l => l.priority>0)
    .sort((l1, l2) => l1.priority - l2.priority)
    .forEach(layer => {
        result.push( <tr id={layer.id}>
                      <td>{means.filter(m => m.id == layer.meanid)[0].title}</td>
                      <td>Layer {layer.depth}</td>
                      <td>{layer.priority}</td>
                    </tr>)
                    })


  return  <Table striped bordered condensed hover >
            <tbody>
              <tr>
                <td>Mean</td>
                <td>Layer</td>
                <td></td>
              </tr>
              {result}
            </tbody>
            </Table>
}
