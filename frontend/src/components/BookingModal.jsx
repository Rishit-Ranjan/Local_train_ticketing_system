import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, User, ChevronRight, ChevronLeft, CreditCard, Wallet as WalletIcon, Check } from 'lucide-react';
import SeatSelection from './SeatSelection';
import { bookingService } from '../services/api';

const BookingModal = ({ schedule, onClose, onSuccess }) => {
    const [step, setStep] = useState(1);
    const [passengerCount, setPassengerCount] = useState(1);
    const [passengers, setPassengers] = useState([{ name: '', age: '', gender: 'MALE' }]);
    const [selectedSeats, setSelectedSeats] = useState([]);
    const [paymentMethod, setPaymentMethod] = useState('WALLET'); // WALLET or CREDIT_CARD
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handlePassengerCountChange = (e) => {
        const count = parseInt(e.target.value) || 1;
        setPassengerCount(count);
        // Adjust passengers array
        if (count > passengers.length) {
            const newPassengers = [...passengers];
            for (let i = passengers.length; i < count; i++) {
                newPassengers.push({ name: '', age: '', gender: 'MALE' });
            }
            setPassengers(newPassengers);
        } else {
            setPassengers(passengers.slice(0, count));
        }
        // Reset seats if count changes
        setSelectedSeats([]);
    };

    const updatePassenger = (index, field, value) => {
        const newPassengers = [...passengers];
        newPassengers[index][field] = value;
        setPassengers(newPassengers);
    };

    const handleNext = () => {
        if (step === 1) {
            // Validate passengers
            for (const p of passengers) {
                if (!p.name || !p.age) {
                    setError('Please fill in all passenger details.');
                    return;
                }
            }
            setError('');
            setStep(2);
        } else if (step === 2) {
            if (selectedSeats.length !== passengerCount) {
                setError(`Please select exactly ${passengerCount} seats.`);
                return;
            }
            setError('');
            setStep(3);
        }
    };

    const handleBooking = async () => {
        setLoading(true);
        setError('');
        try {
            // Map seats to passengers
            const passengersWithSeats = passengers.map((p, index) => ({
                ...p,
                seatNumber: selectedSeats[index]
            }));

            const bookingData = {
                scheduleId: schedule.id,
                travelClass: 'SECOND_CLASS',
                paymentMethod: paymentMethod,
                journeyDate: schedule.departureTime.split('T')[0], // Assuming same day booking for now
                passengers: passengersWithSeats
            };

            await bookingService.createBooking(bookingData);
            onSuccess();
            onClose();
        } catch (err) {
            setError(err.response?.data?.message || 'Booking failed.');
        } finally {
            setLoading(false);
        }
    };

    const totalFare = passengerCount * 50; // hardcoded for now or derived? Schedule doesn't have price. Assuming $50/pax.

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
            <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.9 }}
                className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl overflow-hidden flex flex-col max-h-[90vh]"
            >
                {/* Header */}
                <div className="bg-indigo-600 p-6 flex justify-between items-center text-white">
                    <div>
                        <h2 className="text-xl font-bold">Book Ticket</h2>
                        <p className="text-indigo-100 text-sm">{schedule.train.trainName} • {schedule.sourceStation.stationName} → {schedule.destinationStation.stationName}</p>
                    </div>
                    <button onClick={onClose} className="p-2 hover:bg-white/20 rounded-full transition-colors"><X className="w-5 h-5" /></button>
                </div>

                {/* Body */}
                <div className="p-6 overflow-y-auto flex-1">
                    {/* Progress Bar */}
                    <div className="flex justify-between items-center mb-8 px-4">
                        <div className={`flex flex-col items-center ${step >= 1 ? 'text-indigo-600' : 'text-gray-400'}`}>
                            <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold mb-1 ${step >= 1 ? 'bg-indigo-100' : 'bg-gray-100'}`}>1</div>
                            <span className="text-xs">Passengers</span>
                        </div>
                        <div className={`flex-1 h-0.5 mx-2 ${step >= 2 ? 'bg-indigo-600' : 'bg-gray-200'}`}></div>
                        <div className={`flex flex-col items-center ${step >= 2 ? 'text-indigo-600' : 'text-gray-400'}`}>
                            <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold mb-1 ${step >= 2 ? 'bg-indigo-100' : 'bg-gray-100'}`}>2</div>
                            <span className="text-xs">Seats</span>
                        </div>
                        <div className={`flex-1 h-0.5 mx-2 ${step >= 3 ? 'text-indigo-600' : 'bg-gray-200'}`}></div>
                        <div className={`flex flex-col items-center ${step >= 3 ? 'text-indigo-600' : 'text-gray-400'}`}>
                            <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold mb-1 ${step >= 3 ? 'bg-indigo-100' : 'bg-gray-100'}`}>3</div>
                            <span className="text-xs">Payment</span>
                        </div>
                    </div>

                    {error && <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-lg text-sm">{error}</div>}

                    {step === 1 && (
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Number of Passengers</label>
                                <select
                                    className="w-full p-2 border rounded-lg"
                                    value={passengerCount}
                                    onChange={handlePassengerCountChange}
                                >
                                    {[1, 2, 3, 4, 5, 6].map(n => <option key={n} value={n}>{n}</option>)}
                                </select>
                            </div>

                            <div className="space-y-3">
                                {passengers.map((p, i) => (
                                    <div key={i} className="p-4 bg-gray-50 rounded-lg border border-gray-100">
                                        <h4 className="font-semibold text-gray-700 mb-2 flex items-center"><User className="w-4 h-4 mr-2" /> Passenger {i + 1}</h4>
                                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                                            <input
                                                placeholder="Name"
                                                className="p-2 border rounded-md"
                                                value={p.name}
                                                onChange={e => updatePassenger(i, 'name', e.target.value)}
                                            />
                                            <input
                                                placeholder="Age"
                                                type="number"
                                                className="p-2 border rounded-md"
                                                value={p.age}
                                                onChange={e => updatePassenger(i, 'age', e.target.value)}
                                            />
                                            <select
                                                className="p-2 border rounded-md"
                                                value={p.gender}
                                                onChange={e => updatePassenger(i, 'gender', e.target.value)}
                                            >
                                                <option value="MALE">Male</option>
                                                <option value="FEMALE">Female</option>
                                                <option value="OTHER">Other</option>
                                            </select>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {step === 2 && (
                        <div className="space-y-4">
                            <SeatSelection
                                scheduleId={schedule.id}
                                passengerCount={passengerCount}
                                onSeatsSelected={setSelectedSeats}
                            />
                            <p className="text-sm text-gray-500 text-center mt-2">
                                Please select <b>{passengerCount}</b> seat(s).
                            </p>
                        </div>
                    )}

                    {step === 3 && (
                        <div className="space-y-6">
                            <div className="bg-gray-50 p-4 rounded-lg">
                                <h3 className="font-bold text-gray-800 mb-2">Booking Summary</h3>
                                <div className="flex justify-between text-sm text-gray-600 mb-1">
                                    <span>Train Fare x {passengerCount}</span>
                                    <span>${totalFare.toFixed(2)}</span>
                                </div>
                                <div className="flex justify-between text-lg font-bold text-gray-900 border-t border-gray-200 pt-2 mt-2">
                                    <span>Total Amount</span>
                                    <span>${totalFare.toFixed(2)}</span>
                                </div>
                            </div>

                            <div>
                                <h3 className="font-bold text-gray-800 mb-3">Payment Method</h3>
                                <div className="grid grid-cols-2 gap-4">
                                    <button
                                        className={`p-4 rounded-xl border-2 flex flex-col items-center gap-2 transition-all ${paymentMethod === 'WALLET' ? 'border-indigo-600 bg-indigo-50 text-indigo-700' : 'border-gray-200 hover:border-indigo-200'}`}
                                        onClick={() => setPaymentMethod('WALLET')}
                                    >
                                        <WalletIcon className="w-6 h-6" />
                                        <span className="font-medium">My Wallet</span>
                                    </button>
                                    <button
                                        className={`p-4 rounded-xl border-2 flex flex-col items-center gap-2 transition-all ${paymentMethod === 'CREDIT_CARD' ? 'border-indigo-600 bg-indigo-50 text-indigo-700' : 'border-gray-200 hover:border-indigo-200'}`}
                                        onClick={() => setPaymentMethod('CREDIT_CARD')}
                                    >
                                        <CreditCard className="w-6 h-6" />
                                        <span className="font-medium">Card</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-gray-100 flex justify-between">
                    <button
                        onClick={() => step > 1 ? setStep(step - 1) : onClose()}
                        className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg flex items-center font-medium"
                    >
                        <ChevronLeft className="w-4 h-4 mr-1" /> Back
                    </button>

                    {step < 3 ? (
                        <button
                            onClick={handleNext}
                            className="bg-indigo-600 text-white px-6 py-2 rounded-lg font-bold hover:bg-indigo-700 flex items-center"
                        >
                            Next <ChevronRight className="w-4 h-4 ml-1" />
                        </button>
                    ) : (
                        <button
                            onClick={handleBooking}
                            disabled={loading}
                            className="bg-green-600 text-white px-8 py-2 rounded-lg font-bold hover:bg-green-700 flex items-center shadow-lg shadow-green-200"
                        >
                            {loading ? 'Processing...' : <><Check className="w-4 h-4 mr-2" /> Pay & Book</>}
                        </button>
                    )}
                </div>
            </motion.div>
        </div>
    );
};

export default BookingModal;
