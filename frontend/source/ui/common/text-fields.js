import React from 'react';
import ReactDOM from 'react-dom';

const fieldNameStyle = {textAlign:'right', borderRight:'1px solid lightgrey'}
const fieldStyle = {paddingLeft:'3px'}

export class TextFields extends React.Component{
  constructor(props){
    super(props)
  }

  render(){
    return <table class='textfields-table-style'>
              {linesUI(this.props.content)}
            </table>
  }
}

const linesUI = function(content){
  const result = []
  for(var i in content){
    result.push( <tr key={content[i].key}>
                    <td style={fieldNameStyle}>
                      <div style={{paddingRight:'3px'}}>
                        {content[i].label}
                      </div>
                    </td>
                    <td style={fieldStyle}>
                      {content[i].field}
                    </td>
                  </tr>)
  }
  return result
}
