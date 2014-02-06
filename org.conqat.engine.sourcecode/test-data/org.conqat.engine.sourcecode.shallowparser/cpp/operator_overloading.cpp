Attribute& Attribute::operator =(const Attribute& that) {
  name = that.name;
  value = that.value;
  return *this;
}
