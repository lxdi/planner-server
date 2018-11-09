import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {AllMeans, AddMean, DeleteMeanById, UpdateMean, DeleteMeanWithTarget, CreateMean} from './../../data/means-dao'
import {Button, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {MeanModal} from './mean-modal'
import {ObserversDeleteTarget, ObserversUpdateTarget} from './../../data/targets-dao'

const defaultState = function(){
  return {
    isMeanModalOpen:false,
    parent: null,
    currentMean: CreateMean(0, '', []),
    modalMode: {isStatic: true, isEdit: false}
  }
}

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = defaultState()
    this.openMeanModal = this.openMeanModal.bind(this);
    this.closeMeanModalCallback = this.closeMeanModalCallback.bind(this);
    this.addNewMeanModal = this.addNewMeanModal.bind(this);
    this.meanModalCreateNewOpen = this.meanModalCreateNewOpen.bind(this)

    var uiUpdate = function(){
      this.setState({})
    }.bind(this)

    AllMeans(uiUpdate);

    ObserversDeleteTarget.push(function(id){
      DeleteMeanWithTarget(id, uiUpdate)
    })
    ObserversUpdateTarget.push(uiUpdate)
  }

  //Open existing mean in modal
  openMeanModal(mean){
    this.setState({
      isMeanModalOpen:true,
      currentMean: mean,
      parent: null,
      modalMode: {isStatic: false, isEdit: false}
    })
  }

  //Create new mean as child to existing one
  addNewMeanModal(parentMean){
    this.setState({
      isMeanModalOpen:true,
      currentMean: CreateMean(0, '', []),
      parent: parentMean,
      modalMode: {isStatic: true, isEdit: true}
    })
  }

  //Create new mean in root, Create New Button handler
  meanModalCreateNewOpen(){
    this.setState({
      isMeanModalOpen:true,
      currentMean: CreateMean(0, '', []),
      parent: null,
      modalMode: {isStatic: true, isEdit: true}
    })
  }

  closeMeanModalCallback(eventtype, mean){
    if(eventtype == 'create'){
      AddMean(mean, this.state.parent, function(){
        this.setState({})
      }.bind(this))
    }
    if(eventtype == 'modify'){
      UpdateMean(mean, function(){
        this.setState({})
      }.bind(this))
    }
    if(eventtype == 'delete'){
      DeleteMeanById(mean.id, function(){
        this.setState({})
      }.bind(this))
    }
    this.setState(defaultState())
  }

  render(){
    return(
      <div>
        <div style={{'margin-bottom': '3px'}}>
          <Button bsStyle="primary" bsSize="xsmall" onClick={this.meanModalCreateNewOpen}>
            {createNewMeanButtonTitle}
          </Button>
          <MeanModal isOpen={this.state.isMeanModalOpen}  closeCallback={this.closeMeanModalCallback} currentMean={this.state.currentMean} mode={this.state.modalMode}/>
        </div>
        <div>
          <ListGroup>
            {meansUIlist(this.openMeanModal, this.addNewMeanModal, this.state.means)}
          </ListGroup>
        </div>
      </div>
    )
  }
}

const meansUIlist = function(editOpenHandler, addHandler, means){
  return AllMeans().map(function(mean){
      return <ListGroupItem>
        {meanUI(mean, editOpenHandler, addHandler, 20)}
      </ListGroupItem>
  }, function(mean){
    return mean.parentid==null
  })
}

const meanUI = function(mean, editOpenHandler, addHandler, offset){
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
        <a href="#" onClick={editOpenHandler.bind(this, mean)}>
           {mean.children.length==0?
              <span style={{'font-weight': 'bold'}}>{mean.title}</span>
              :mean.title}
         </a>
          <span style={{color: 'green'}}> {mean.targetsString()}</span>  <span/>
      <a href="#" onClick={addHandler.bind(this, mean)}>
          {addNewMeanTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {mean.children.map(function(childMean){
            return <li>
              {meanUI(childMean, editOpenHandler, addHandler, offset + 10)}
            </li>
        })}
      </div>
    </div>
  )
}
