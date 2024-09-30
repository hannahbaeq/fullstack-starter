import * as Yup from 'yup'
import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import Grid from '@material-ui/core/Grid'
import { MeasurementUnits } from '../../constants/units'
import { MenuItem } from '@material-ui/core'
import React from 'react'
import TextField from '../Form/TextField'
import { Field, Form, Formik } from 'formik'

class InventoryFormModal extends React.Component {
  render() {
    const {
      formName,
      handleDialog,
      handleInventory,
      title,
      initialValues,
      products
    } = this.props

    const validate = Yup.object().shape({
      name: Yup.string().required('Name Required'),
      productType: Yup.string().required('Product Type Required'),
      amount: Yup.number().required('Amount Required').min(0, 'Must be at least 0'),
      unitOfMeasurement: Yup.string().required('Unit of Measurement Required'),
      bestBeforeDate: Yup.date().required('Expiration Date Required').min('1900-12-31')
    })

    return (
      <Dialog
        open={this.props.isDialogOpen}
        maxWidth='sm'
        fullWidth={true}
        onClose={() => { handleDialog(false) }}
      >
        <Formik
          initialValues={initialValues}
          validationSchema={validate}
          onSubmit={values => { console.log(values)
            const instantDate = new Date(values.bestBeforeDate)
            values.bestBeforeDate = instantDate.toISOString()
            handleInventory(values)
            handleDialog(true)
          }}>
          {helpers =>
            <Form
              noValidate
              autoComplete='off'
              id={formName}
            >
              <DialogTitle id='alert-dialog-title'>
                {`${title} Inventory`}
              </DialogTitle>
              <DialogContent>
                <Grid container
                  flex={1}
                  spacing={2}>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='name'
                      label='Name'
                      component={TextField}
                    />
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='productType'
                      label='Product'
                      component={TextField}
                      select
                    >
                      { products ?
                        products.map(product =>
                          <MenuItem key={product.id} value={product.name}>
                            {product.name}
                          </MenuItem>
                        ) :
                        <MenuItem disabled>No products available</MenuItem>
                      }
                    </Field>
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='description'
                      label='Description'
                      component={TextField}
                    />
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='amount'
                      label='Amount'
                      component={TextField}
                      type='number'
                    />
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='unitOfMeasurement'
                      label='Unit of Measurement'
                      component={TextField}
                      select
                    >
                      { Object.keys(MeasurementUnits).map(key =>
                        <MenuItem key={key} value={key}>
                          {key}
                        </MenuItem>
                      )}
                    </Field>
                  </Grid>
                  <Grid item xs={12} sm={12}>
                    <label htmlFor="bestBeforeDate"> Best Before Date</label>
                    <Field
                      custom={{ variant: 'outlined', fullWidth: true, }}
                      name='bestBeforeDate'
                      component={TextField}
                      type='date'
                    />
                  </Grid>
                </Grid>
              </DialogContent>
              <DialogActions>
                <Button onClick={() => { handleDialog(false) }} color='secondary'>Cancel</Button>
                <Button
                  disableElevation
                  variant='contained'
                  type='submit'
                  form={formName}
                  color='secondary'
                  disabled={!helpers.dirty}>
                  Save
                </Button>
              </DialogActions>
            </Form>
          }
        </Formik>
      </Dialog>
    )
  }
}

export default InventoryFormModal
