import React from "react";
import { Button, Container, Form, InputGroup } from "react-bootstrap";
import bsCustomFileInput from "bs-custom-file-input";
import "../css/Upload.css";

interface MyState {
  style: React.CSSProperties,
}

class Upload extends React.Component<{}, MyState> {

  /**
   * Creates an instance of Upload.
   * @param {*} props props
   * @memberof Upload
   */
  public constructor(props: any) {
    super(props);

    this.state = {
      style: {} as React.CSSProperties
    };

    this.updateDimensions = this.updateDimensions.bind(this);
  }

  /**
   * Update the dimensions
   *
   * @returns {void} void
   * @memberof Upload
   */
  private updateDimensions(): void {
    const nav = document.getElementById("navBar");

    if (nav !== null) {
      this.setState({
        style: {
          width: `${window.innerWidth}px`,
          height: `${window.innerHeight - nav.offsetHeight}px`
        } as React.CSSProperties
      });
    }
  }

  /**
   * Initialise bsCustomFileInput
   *
   * @returns {void} nothing
   * @memberof Upload
   */
  public componentDidMount(): void {
    bsCustomFileInput.init();
    this.updateDimensions();
    window.addEventListener("resize", this.updateDimensions);
  }

  /**
   * Render the upload form.
   *
   * @returns {JSX.Element} The element to be rendered
   * @memberof Upload
   */
  public render(): JSX.Element {
    return (
      <div style={this.state.style}>
        <Container className="Upload h-100 d-flex">
          <Form
            id="uploadForm"
            className="align-self-center"
            action="/api/upload"
            method="post"
            encType="multipart/form-data">
            <Form.Group>
              <InputGroup>
                <div className="custom-file">
                  <input className="custom-file-input" id="fileUpload" type="file" accept=".xlsx" name="excel" required />
                  <label className="custom-file-label" htmlFor="fileUpload">Choose file</label>
                </div>
              </InputGroup>
            </Form.Group>
            <Form.Group>
              <Form.Label>From Date</Form.Label>
              <Form.Control type="date" name="from" required />
            </Form.Group>
            <Form.Group>
              <Form.Label>To Date</Form.Label>
              <Form.Control type="date" name="to" required />
            </Form.Group>
            <Button type="submit">
              Roster
            </Button>
          </Form>
        </Container>
      </div>
    );
  }

}

export default Upload;
