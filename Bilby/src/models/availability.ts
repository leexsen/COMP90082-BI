export interface Availability {
  availableFrom: Date,
  availableUntil: Date,
}

export interface Unavailability {
  notAvailableFrom: Date,
  notAvailableUntil: Date,
  notes: string,
}
