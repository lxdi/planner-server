import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {AllMeans, CreateMean} from './../../data/means-dao'
import {Button, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {MeanModal} from './mean-modal'
import {registerEvent, registerReaction, fireEvent} from '../../controllers/eventor'

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

    registerReaction('means-frame', 'targets-dao', 'target-deleted', (targetid)=>{
      fireEvent('means-dao', 'delete-depended-means', [targetid])
      this.setState({})
    })

    AllMeans(uiUpdate);
  }

  render(){
    return(
      <div>
        <div style={{'margin-bottom': '3px'}}>
          <Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', [])])}>
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
  return AllMeans().map(function(mean){
      return <ListGroupItem>
        {meanUI(mean, 20)}
      </ListGroupItem>
  }, function(mean){
    return mean.parentid==null
  })
}

const meanUI = function(mean, offset){
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
        <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [mean])}>
           {mean.children.length==0?
              <span style={{'font-weight': 'bold'}}>{mean.title}</span>
              :mean.title}
         </a>
          <span style={{color: 'green'}}> {mean.targetsString()}</span>  <span/>
      <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', []), mean])}>
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
