import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CreateMean} from './../../data/creators'
import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {MeanModal} from './mean-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'
import {TreeComponent} from './../components/tree-component'

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {isEdit: false}
    registerReaction('means-frame', 'targets-dao', 'target-deleted', (state, targetid)=>{
      fireEvent('means-dao', 'delete-depended-means', [targetid])
      this.setState({})
    })

    registerReaction('means-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
    registerReaction('means-frame', 'means-dao',
            ['means-received', 'replace-mean', 'mean-created', 'mean-deleted', 'mean-modified', 'means-list-modified', 'draggable-add-as-child'], ()=>this.setState({}))
  }

  render(){
    return(
      <div>
        {viewStateVal('realms-dao', 'currentRealm')!=null?
          <div style={{'margin-bottom': '3px'}}>
            {getControlButtons(this)}
            <MeanModal/>
          </div>:null}
          {meansUIlist(this)}
      </div>
    )
  }
}

const getControlButtons = function(component){
  const result = []
  result.push(<Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, [])])}>
                {createNewMeanButtonTitle}
              </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const meansUIlist = function(component){
  if(viewStateVal('means-dao', 'means')!=null){
      if(viewStateVal('realms-dao', 'currentRealm')!=null){
        return <TreeComponent isEdit={component.state.isEdit}
                  nodes={viewStateVal('means-dao', 'means')[viewStateVal('realms-dao', 'currentRealm').id]}
                  viewCallback = {(mean)=>meanUI(component, mean)}
                  onDropCallback = {(alteredList)=>{fireEvent('means-dao', 'modify-list', [alteredList]); fireEvent('means-dao', 'remove-draggable')}}
                  onDragStartCallback = {(mean)=>fireEvent('means-dao', 'add-draggable', [mean])}
                  rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px', padding:'3px'}}
                  shiftpx={15}
                  />
      }
      return ''
    }
  return 'Loading...'
}

const meanUI = function(component, mean){
  return <div style={mean.parentid!=null?{borderLeft:'1px solid grey', paddingLeft:'3px'}:null}>
                    <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [mean])}>
                        {mean.title}
                    </a>
                    <a href="#" style = {{marginLeft:'3px'}} onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, []), mean])}>
                      {addNewMeanTitle}
                    </a>
                    <span style={{color: 'green', fontSize:'8pt'}}> {mean.targetsString()}</span>
                </div>
}
