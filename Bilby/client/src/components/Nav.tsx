import React from "react";
import { Navbar } from "react-bootstrap";
import "../css/Nav.css";
import "bootstrap/dist/css/bootstrap.min.css";

class Nav extends React.Component {

  /**
   * Render the Nav.
   *
   * @returns {JSX.Element} Element to be rendered
   * @memberof Nav
   */
  public render(): JSX.Element {
    return (
      <Navbar id="navBar" bg="bi" variant="dark">
        <Navbar.Brand>
          <img
            alt=""
            src="logo.jpg"
            // width="30"
            height="50"
            className="d-inline-block align-top"
          />
          {/* {"Rostering"} */}
        </Navbar.Brand>
      </Navbar>
    );
  }

}

export default Nav;
