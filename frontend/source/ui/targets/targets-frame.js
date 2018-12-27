import {createNewTargetButtonTitle, addNewTargetTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, FormGroup, ControlLabel, FormControl, ListGroup, ListGroupItem, ButtonGroup} from 'react-bootstrap'
import {CreateTarget, CreateRealm} from './../../data/creators'
import {TargetModal} from './target-modal'
import {RealmModal} from './realm-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'
import {iterateLLfull} from '../../utils/linked-list'
import {mergeArrays, resolveNodes, replaceDraggableUtil, addAsChildDraggableUtil} from '../../utils/draggable-tree-utils'

import {TreeComponent} from './../tree-component'

const offsetVal = 10

export class TargetsFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {}
    this.onDragOver = this.onDragOver.bind(this)
    this.onDrop = this.onDrop.bind(this)

    registerReaction('targets-frame', 'targets-dao', 'targets-received', ()=>this.setState({}))
    registerReaction('targets-frame', 'realms-dao', ['realms-received', 'change-current-realm', 'realm-created'], ()=>this.setState({}))

    registerReaction('targets-frame', 'targets-dao',
            ['targets-received', 'replace-target', 'target-created', 'target-deleted', 'target-modified', 'targets-list-modified', 'draggable-add-as-child'], ()=>this.setState({}))

  }

  onDragOver(target, parentTarget, type){
    var altered = null
    if(type=='replace'){
      altered = replaceDraggableUtil(this.state.allNodes, parentTarget, target, this.state.draggableTarget, this.state.cache)
    }
    if(type=='addchild'){
      altered = addAsChildDraggableUtil(this.state.allNodes, target, this.state.draggableTarget, this.state.cache)
    }
    if(this.state.altered==null){
      this.setState({altered: altered})
    } else {
      mergeArrays(this.state.altered, altered)
      this.setState({})
    }
  }

  onDrop(){
    if(this.state.altered!=null && this.state.altered.length>0){
      fireEvent('targets-dao', 'modify-list', [this.state.altered])
    }
    fireEvent('targets-dao', 'remove-draggable', [])
    this.setState({altered:null, draggableTarget:null})
  }

  render(){

    var testNodes = {
      1:{id:1, nextid:2, parentid:null},
      2:{id:2, nextid:null, parentid:null},
      3:{id:3, nextid:4, parentid:1},
      4:{id:4, nextid:null, parentid:1},
      5:{id:5, nextid:null, parentid:4}
    }

    var viewCallback = function(node){
      if(node.parentid==null){
        return <div>Node with id {node.id}</div>
      } else {
        return <li>Node with id {node.id}</li>
      }
    }

    return(
      <div>
        <TargetModal/>
        <RealmModal/>
        <div>
          <Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('realm-modal', 'open', [CreateRealm(0, '')])}>
            Create New Realm
          </Button>
          <div onDrop={this.onDrop} onDragOver={(e)=>e.preventDefault()}>
            <ListGroup>
              {realmsUI(this)}
            </ListGroup>
          </div>
        </div>
        <TreeComponent nodes={testNodes} viewCallback={viewCallback} rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px'}}/>
      </div>
    )
  }
}

const realmsUI = function(component){
  if(viewStateVal('realms-dao', 'realms')!=null){
    if(viewStateVal('targets-dao', 'targets')!=null){
      const result = []
      const realms = viewStateVal('realms-dao','realms')
      for(var realmId in realms){
        const realmidConst = realmId
        const isCurrentRealm = realms[realmidConst]==viewStateVal('realms-dao','currentRealm')
        result.push(<ListGroupItem key={"realm_"+realmidConst+(realms[realmidConst]==viewStateVal('realms-dao','currentRealm')?"_current":"_notcurrent")}>
            <div>
              <h4 onClick={()=>fireEvent('realms-dao', 'change-current-realm', [realms[realmidConst]])}>
                <input type="radio" autocomplete="off"
                  checked={isCurrentRealm?"checked":null}/>
                {realms[realmId].title}
              </h4>
            </div>
            <div>
              {isCurrentRealm?targetsUI(component):null}
            </div>
          </ListGroupItem>)
      }
      return result
    } else {
      fireEvent('targets-dao', 'targets-request', [])
      return null
    }
  } else {
    fireEvent('realms-dao', 'realms-request', [])
    return null
  }
}

const targetsUI = function(component){
  return <div>
          <div>{getControlButtonsForTargets(component)}</div>
          <div>{targetsUIlist(component)}</div>
    </div>
}

const getControlButtonsForTargets = function(component){
  const result = []
  result.push(<Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', viewStateVal('realms-dao','currentRealm').id)])}>
              {createNewTargetButtonTitle}
            </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const styleForAddAsChild = {width:'80px', color:'lightgrey', fontSize: '10px', border: '1px dotted lightgrey', borderRadius: '5px', padding: '3px'}

const targetsUIlist = function(component){
      const result = []
      if(viewStateVal('realms-dao', 'currentRealm')!=null){
        component.state.allNodes = viewStateVal('targets-dao', 'targets')[viewStateVal('realms-dao', 'currentRealm').id]
        component.state.cache = resolveNodes(component.state.allNodes)
        iterateLLfull(component.state.cache.root, (target)=>{
          result.push(<ListGroupItem key={'target_'+target.id}>{targetUI(component, target, 20)}</ListGroupItem>)
        })
      }
      return result
}

const targetUI = function(component, target, offset){
  const parentTarget = target.parentid!=null? component.state.allNodes[target.parentid]:null
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
         {draggableElem(component,
             countFields(component.state.cache.children[target.id])==0?<span style={{'font-weight': 'bold'}}>{target.title}</span>:target.title,
             parentTarget, target, ()=>fireEvent('target-modal', 'open', [target]))}
        <a href="#" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', viewStateVal('realms-dao', 'currentRealm').id, []), target])}>
          {addNewTargetTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {targetChildrenUI(component, component.state.cache.children[target.id], offset)}
        {component.state.isEdit && countFields(component.state.cache.children[target.id])==0?
          <div style={styleForAddAsChild}
                onDragOver={(e)=>{e.preventDefault(); component.onDragOver(target, null, 'addchild')}}>+ Add as a child</div>
          :null}
      </div>
    </div>
  )
}

const targetChildrenUI = function(component, children, offset){
  const result = []
  if(children!=null){
    iterateLLfull(children, (target)=>{
      result.push(<li key={'target_'+target.id}>
        {targetUI(component, target, offset + offsetVal)}
      </li>)
    })
  }
  return result
}

const draggableElem = function(component, content, parentTarget, target, onClick){
  if(component.state.isEdit){
    return  <a href="#" onClick={onClick}
              draggable='true'
              onDragStart={()=>{component.state.draggableTarget = target; fireEvent('targets-dao', 'add-draggable', [target])}}
              onDragOver={(e)=>{e.preventDefault(); component.onDragOver(target, parentTarget, 'replace')}}>
               {content}
             </a>
  } else {
    return  <a href="#" onClick={onClick} draggable='true' onDragStart={()=>{fireEvent('targets-dao', 'add-draggable', [target])}}>
              {content}
            </a>
  }
}

const countFields = (obj)=>{
  var result = 0;
  if(obj!=null){
    for(var i in obj){result++};
  }
  return result
}
