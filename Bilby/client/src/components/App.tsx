import React from "react";
import "../css/App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import Nav from "./Nav";
import Upload from "./Upload";

class App extends React.Component {

  /**
   * Render the app.
   *
   * @returns {JSX.Element} Element to be rendered
   * @memberof App
   */
  public render(): JSX.Element {
    return (
      <div className="App h-100">
        <Nav />
        <Upload />
      </div>
    );
  }

}

export default App;
