import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {TreeComponent} from './../components/tree-component'

import {CreateMean, CreateRealm} from './../../data/creators'
import {MeanModal} from './modal/mean-modal'
import {TaskModal} from './modal/task-modal'
import {RealmModal} from './realm-modal'

const newId = 'new'
const currentRealm = 'currentRealm'
const indexByRealmid = 'index-by-realmid'

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {isEdit: false}

    registerEvent('means-frame', 'update', ()=>this.setState({}))

    registerReaction('means-frame', 'realm-rep', ['all-response', 'change-current-realm', 'created'], ()=>{this.setState({})})

    registerReaction('means-frame', 'mean-rep',
            ['all-response', 'created', 'deleted', 'updated',
              'replace-mean', 'repositioned',
            'draggable-add-as-child', 'hide-children-changed'], ()=>this.setState({}))
  }

  render(){
    return(
      <div>
        {getControlButtons(this)}
        <MeanModal/>
        <RealmModal/>
        <TaskModal/>
        <div>
          <ListGroup>
            {realmsUI(this)}
          </ListGroup>
        </div>
      </div>
    )
  }
}


const getControlButtons = function(component){
  const result = []

  result.push(<Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('realm-modal', 'open', [CreateRealm(newId, '')])}>
              New Realm
            </Button>)

  result.push(<Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(newId, '', chkSt('realm-rep', currentRealm).id)])}>
                New Means
              </Button>)

  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)

  return <ButtonGroup>{result}</ButtonGroup>
}

const realmsUI = function(component){
  const realmsMap = chkSt('realm-rep','objects')

  if (realmsMap == null) {
    fireEvent('realm-rep', 'all-request', [])
    return null
  }

  const realms = Object.values(realmsMap)
  realms.sort((r1, r2) => r1.priority - r2.priority)
  const means = chkSt('mean-rep', 'objects')

  if (means == null) {
    fireEvent('mean-rep', 'all-request', [])
    return null
  }

  const result = []

  for(var i in realms){
    const realm = realms[i]

    result.push(<ListGroupItem key={"realm_"+realm.id+(realm.current?"_current":"_notcurrent")}>
            <div>
              <h4 onClick={()=>fireEvent('realm-rep', 'change-current-realm', [realm])}>
                <input type="radio" autocomplete="off" checked={realm.current?"checked":null} style={{marginRight:'5px'}} />
                {realm.title}
              </h4>
            </div>
            <div>
              {realm.current?meansUIlist(component):null}
            </div>
          </ListGroupItem>)
  }
  return result
}

const meansUIlist = function(component){

  if (chkSt('mean-rep', 'objects')==null) {
      fireEvent('mean-rep', 'all-request')
      return 'Loading...'
  }

  const curRealm = chkSt('realm-rep', currentRealm)

  if (curRealm==null){
    return ''
  }

  return <TreeComponent isEdit={component.state.isEdit}
                  nodes={chkSt('mean-rep', indexByRealmid)[curRealm.id]}
                  viewCallback = {(mean)=>meanUI(component, mean)}
                  onDropCallback = {(alteredList)=>{fireEvent('mean-rep', 'reposition', [alteredList])}}
                  onDragStartCallback = {(mean, e)=> fireEvent('drag-n-drop', 'put', ['assign-mean', mean])}
                  rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px', padding:'3px'}}
                  shiftpx={15}
                  />
}

const meanUI = function(component, mean){
  var meanLinkStyle = {}

  return <div style={mean.parentid!=null?{borderLeft:'1px solid grey', paddingLeft:'3px'}:null}>
                    {hideShowChildrenControlUI(component, mean)}
                    <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [mean])} style={meanLinkStyle}>
                        {markDraggableMeanTitle(mean)}
                    </a>
                    <a href="#" style = {{marginLeft:'3px'}} onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(newId, '', chkSt('realm-rep', currentRealm).id, mean.id)])}>
                      {addNewMeanTitle}
                    </a>
                </div>
}

const markDraggableMeanTitle = function(mean){
  if(chkSt('mean-rep', 'draggableMean')==mean){
    return <strong>{mean.title}</strong>
  } else {
    return mean.title
  }
}

const hideShowChildrenControlUI = function(component, mean){
  return <a href="#" style = {{marginRight:'3px'}} onClick={()=>{
      mean.hideChildren = !mean.hideChildren
      fireEvent('mean-rep', 'hide-children', [mean])
    }}>
    {mean.hideChildren==null || (mean.hideChildren!=null && mean.hideChildren==false)?'-':'+'}
  </a>
}
