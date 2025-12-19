import React, { useState, useEffect } from 'react';
import { bookingService } from '../services/api';

const SeatSelection = ({ scheduleId, passengerCount, onSeatsSelected }) => {
    const [bookedSeats, setBookedSeats] = useState([]);
    const [selectedSeats, setSelectedSeats] = useState([]);
    const [loading, setLoading] = useState(true);

    // Simple layout: 10 rows, 4 seats per row (A, B, C, D)
    const rows = 10;
    const cols = ['A', 'B', 'C', 'D'];

    useEffect(() => {
        fetchBookedSeats();
    }, [scheduleId]);

    const fetchBookedSeats = async () => {
        try {
            setLoading(true);
            const res = await bookingService.getBookedSeats(scheduleId);
            setBookedSeats(res.data);
        } catch (error) {
            console.error("Failed to fetch seat availability", error);
        } finally {
            setLoading(false);
        }
    };

    const handleSeatClick = (seatId) => {
        if (bookedSeats.includes(seatId)) return;

        if (selectedSeats.includes(seatId)) {
            setSelectedSeats(selectedSeats.filter(id => id !== seatId));
        } else {
            if (selectedSeats.length < passengerCount) {
                setSelectedSeats([...selectedSeats, seatId]);
            } else {
                // Option to replace the last selected seat? Or just enforce limit.
                // Let's enforce limit by alerting or doing nothing.
                // Actually, let's create a nice UX: if full, don't allow add.
            }
        }
    };

    // Propagate changes to parent
    useEffect(() => {
        if (onSeatsSelected) {
            onSeatsSelected(selectedSeats);
        }
    }, [selectedSeats, onSeatsSelected]);

    return (
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
            <h3 className="text-lg font-bold mb-4">Select Seats ({selectedSeats.length}/{passengerCount})</h3>

            {loading ? (
                <div className="text-center py-8">Loading seat map...</div>
            ) : (
                <div className="flex flex-col items-center space-y-2">
                    <div className="w-full flex justify-between px-8 mb-4 max-w-xs text-sm text-gray-500">
                        <div className="flex items-center gap-2"><div className="w-4 h-4 rounded bg-gray-200 border border-gray-300" /> Available</div>
                        <div className="flex items-center gap-2"><div className="w-4 h-4 rounded bg-gray-400 cursor-not-allowed" /> Booked</div>
                        <div className="flex items-center gap-2"><div className="w-4 h-4 rounded bg-indigo-600 text-white" /> Selected</div>
                    </div>

                    <div className="grid gap-4 max-w-xs mx-auto">
                        {Array.from({ length: rows }).map((_, rIndex) => (
                            <div key={rIndex} className="flex gap-4">
                                {/* A and B */}
                                <div className="flex gap-2">
                                    {cols.slice(0, 2).map(col => {
                                        const seatId = `${col}${rIndex + 1}`;
                                        const isBooked = bookedSeats.includes(seatId);
                                        const isSelected = selectedSeats.includes(seatId);
                                        return (
                                            <button
                                                key={seatId}
                                                disabled={isBooked}
                                                onClick={() => handleSeatClick(seatId)}
                                                className={`w-10 h-10 rounded-lg flex items-center justify-center font-medium text-sm transition-all
                                            ${isBooked
                                                        ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                                                        : isSelected
                                                            ? 'bg-indigo-600 text-white shadow-md transform scale-105'
                                                            : 'bg-white border-2 border-gray-200 hover:border-indigo-400 text-gray-600'
                                                    }`}
                                            >
                                                {seatId}
                                            </button>
                                        );
                                    })}
                                </div>

                                {/* Aisle */}
                                <div className="w-4"></div>

                                {/* C and D */}
                                <div className="flex gap-2">
                                    {cols.slice(2, 4).map(col => {
                                        const seatId = `${col}${rIndex + 1}`;
                                        const isBooked = bookedSeats.includes(seatId);
                                        const isSelected = selectedSeats.includes(seatId);
                                        return (
                                            <button
                                                key={seatId}
                                                disabled={isBooked}
                                                onClick={() => handleSeatClick(seatId)}
                                                className={`w-10 h-10 rounded-lg flex items-center justify-center font-medium text-sm transition-all
                                            ${isBooked
                                                        ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                                                        : isSelected
                                                            ? 'bg-indigo-600 text-white shadow-md transform scale-105'
                                                            : 'bg-white border-2 border-gray-200 hover:border-indigo-400 text-gray-600'
                                                    }`}
                                            >
                                                {seatId}
                                            </button>
                                        );
                                    })}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default SeatSelection;
