// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package distributed.distributed

/** rpc taskDoneReport
  */
@SerialVersionUID(0L)
final case class DoneRequest(
    machineID: _root_.scala.Int = 0,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[DoneRequest] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      
      {
        val __value = machineID
        if (__value != 0) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeInt32Size(1, __value)
        }
      };
      __size += unknownFields.serializedSize
      __size
    }
    override def serializedSize: _root_.scala.Int = {
      var __size = __serializedSizeMemoized
      if (__size == 0) {
        __size = __computeSerializedSize() + 1
        __serializedSizeMemoized = __size
      }
      __size - 1
      
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): _root_.scala.Unit = {
      {
        val __v = machineID
        if (__v != 0) {
          _output__.writeInt32(1, __v)
        }
      };
      unknownFields.writeTo(_output__)
    }
    def withMachineID(__v: _root_.scala.Int): DoneRequest = copy(machineID = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = machineID
          if (__t != 0) __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PInt(machineID)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: distributed.distributed.DoneRequest.type = distributed.distributed.DoneRequest
    // @@protoc_insertion_point(GeneratedMessage[distributed.DoneRequest])
}

object DoneRequest extends scalapb.GeneratedMessageCompanion[distributed.distributed.DoneRequest] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[distributed.distributed.DoneRequest] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): distributed.distributed.DoneRequest = {
    var __machineID: _root_.scala.Int = 0
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 8 =>
          __machineID = _input__.readInt32()
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    distributed.distributed.DoneRequest(
        machineID = __machineID,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[distributed.distributed.DoneRequest] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      distributed.distributed.DoneRequest(
        machineID = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Int]).getOrElse(0)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = DistributedProto.javaDescriptor.getMessageTypes().get(8)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = DistributedProto.scalaDescriptor.messages(8)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = distributed.distributed.DoneRequest(
    machineID = 0
  )
  implicit class DoneRequestLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, distributed.distributed.DoneRequest]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, distributed.distributed.DoneRequest](_l) {
    def machineID: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Int] = field(_.machineID)((c_, f_) => c_.copy(machineID = f_))
  }
  final val MACHINEID_FIELD_NUMBER = 1
  def of(
    machineID: _root_.scala.Int
  ): _root_.distributed.distributed.DoneRequest = _root_.distributed.distributed.DoneRequest(
    machineID
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[distributed.DoneRequest])
}
