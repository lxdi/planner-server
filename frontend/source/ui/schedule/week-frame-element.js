import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {currentDateString} from '../../utils/date-utils'

const dayCellStyle = {border: '1px solid lightgrey', padding: '2px', borderRadius:'5px'}
const todayCellStyle = {border: '1px solid grey', padding: '2px', borderRadius:'5px'}

// props: week
export class WeekElement extends React.Component {
  constructor(props){
    super(props)
  }

  render(){

    if(this.props.week==null){
      return 'Loading...'
    }

    const days = []
    this.props.week.days.forEach(day => {
      days.push(<td>{getDayCellUI(day)}</td>)
    })

    return <div style={{marginBottom:'3px'}}>
            <table style={{borderCollapse:'collapse', width:'100%'}}>
                  <tr>
                    {days}
                  </tr>
            </table>
          </div>
  }

}


const getDayCellUI = function(day){
  var style = null

  if(currentDateString('-') == day.date){
    style = todayCellStyle
  } else {
    style = dayCellStyle
  }

  return <div style={style}>
            {day.weekDay}: 0/0
        </div>
}
