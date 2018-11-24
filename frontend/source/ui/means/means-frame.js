import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CreateMean} from './../../data/creators'
import {Button, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {MeanModal} from './mean-modal'
import {registerEvent, registerReaction, fireEvent, getStateVal} from '../../controllers/eventor'

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {}

    var uiUpdate = function(){
      this.setState({})
    }.bind(this)

    registerReaction('means-frame', 'means-dao', ['mean-created', 'mean-deleted', 'mean-modified'], function(){
      fireEvent('mean-modal', 'close')
      this.setState({})
    }.bind(this))

    registerReaction('means-frame', 'targets-dao', 'target-deleted', (state, targetid)=>{
      fireEvent('means-dao', 'delete-depended-means', [targetid])
      this.setState({})
    })

    registerReaction('means-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
    registerReaction('means-frame', 'targets-dao', 'targets-received', ()=>this.setState({}))
    registerReaction('means-frame', 'means-dao', 'means-received', ()=>this.setState({}))

  }

  render(){
    return(
      <div>
        <div style={{'margin-bottom': '3px'}}>
          <Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', getStateVal('realms-dao', 'currentRealm').id, [])])}>
            {createNewMeanButtonTitle}
          </Button>
          <MeanModal/>
        </div>
        <div>
          <ListGroup>
            {meansUIlist()}
          </ListGroup>
        </div>
      </div>
    )
  }
}

const meansUIlist = function(){
  if(getStateVal('targets-dao', 'targets')!=null){
    if(getStateVal('means-dao', 'means')!=null){
      return getStateVal('means-dao', 'means').map(function(mean){
          return <ListGroupItem>
            {meanUI(mean, 20)}
          </ListGroupItem>
      }, function(mean){
        return mean.parentid==null && mean.realmid == getStateVal('realms-dao', 'currentRealm').id
      })
    } else {
      fireEvent('means-dao', 'means-request', [])
      return null
    }
  }
}

const meanUI = function(mean, offset){
  return (
    <div>
      <div onDragStart={()=>fireEvent('means-dao', 'add-draggable', [mean])} onDragEnd={()=>fireEvent('means-dao', 'remove-draggable', [])} style={{'margin-bottom': '5px'}}>
        <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [mean])}>
           {mean.children.length==0?
              <span style={{'font-weight': 'bold'}}>{mean.title}</span>
              :mean.title}
         </a>
          <span style={{color: 'green'}}> {mean.targetsString()}</span>  <span/>
        <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', getStateVal('realms-dao', 'currentRealm').id, []), mean])}>
          {addNewMeanTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {mean.children.map(function(childMean){
            return <li>
              {meanUI(childMean, offset + 10)}
            </li>
        })}
      </div>
    </div>
  )
}
